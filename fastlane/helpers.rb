require 'rest-client'

def create_java_keystore(private_key_b64:, certificate_b64:, destination_keystore_file:, keystore_password:)
  puts("Creating java keystore #{destination_keystore_file}")
  signing_key_file = "#{Dir.tmpdir()}/signing.key"
  signing_cert_file = "#{Dir.tmpdir()}/signing.crt"
  p12_keystore_file = "#{Dir.tmpdir()}/signing.p12"

  IO.write(signing_key_file, Base64.decode64(private_key_b64))
  IO.write(signing_cert_file, Base64.decode64(certificate_b64))

  sh(
    "openssl pkcs12 "\
      "-export "\
      "-inkey \"#{signing_key_file}\" "\
      "-in \"#{signing_cert_file}\" "\
      "-out \"#{p12_keystore_file}\" "\
      "-passout \"pass:#{keystore_password}\""
  )

  sh(
    "keytool "\
      "-importkeystore "\
      "-srckeystore \"#{p12_keystore_file}\" "\
      "-srcstoretype pkcs12 "\
      "-srcstorepass \"#{keystore_password}\" "\
      "-destkeystore \"#{destination_keystore_file}\" "\
      "-deststoretype JKS "\
      "-storepass \"#{keystore_password}\" "\
      "-noprompt "\
      "2>/dev/null"
  )

  File.delete(signing_key_file, signing_cert_file, p12_keystore_file)
end

# Check if an environment variable exists and is not empty
def env_var_valid?(var_name)
  if ENV.has_key?(var_name) && !ENV[var_name].to_s.empty?
    return true
  else
    puts("Required environment variable #{var_name} is not set or is empty.")
    return false
  end
end

def env_vars_valid?(var_names)
  return var_names.all? { |var_name| env_var_valid?(var_name) }
end

def inject_staging_environment(app:, sdk_root_path:)
  staging_hostname = ENV["STAGING_HOST_NAME"]
  if staging_hostname.nil?
    raise "Staging build requested but no STAGING_HOST_NAME environment variable set. Unable to continue."
  end

  replace(
    file_path: "#{sdk_root_path}/src/main/java/com/judopay/judokit/android/model/ApiEnvironment.kt",
    old_string: ".judopay.com",
    new_string: ".#{staging_hostname}"
  )

  replace(
    file_path: "#{app.path}/src/main/res/values/strings.xml",
    old_string: "JudoKit",
    new_string: "[STG] JudoKit"
  )
end

def revert_staging_environment(app:, sdk_root_path:)
  reset_git_repo(
    force: true,
    files: [
      "#{sdk_root_path}/src/main/java/com/judopay/judokit/android/model/ApiEnvironment.kt",
      "#{app.path}/src/main/res/values/strings.xml"
    ]
  )
end

def remove_certificate_pinner(sdk_root_path:)
  # The regex pattern to match the certificate pinner block
  # Starts with `builder.certificatePinner(`, followed by any number of nested parentheses, and ends with a closing parenthesis
  replace(
    file_path: "#{sdk_root_path}/src/main/java/com/judopay/judokit/android/api/factory/JudoApiServiceFactory.kt",
    old_string: /builder\.certificatePinner\([^()]*(?:\([^()]*(?:\([^()]*\)[^()]*)*\)[^()]*)*\)/m,
    new_string: ''
  )
end

def replace(file_path:, old_string:, new_string:)
  # Check if the file exists
  unless File.file?(file_path)
    puts("File not found: #{file_path}")
  end

  # Replace the string
  new_content = File.read(file_path).gsub(old_string, new_string)

  # Write the updated content back to the file
  File.open(file_path, "w") { |file| file.puts new_content }

  puts("Replaced '#{old_string}' with '#{new_string}' in #{file_path}")
end

restore_certificate_pinner(sdk_root_path:)
  reset_git_repo(
    force: true,
    files: [
      "#{sdk_root_path}/src/main/java/com/judopay/judokit/android/api/factory/JudoApiServiceFactory.kt"
    ]
  )
end

def send_rest_request(url:, method:, payload:, user:, password:)
  begin
    response = RestClient::Request.execute(
      method: method,
      url: url,
      user: user,
      password: password,
      payload: payload
    )
    return JSON.parse(response.to_s)
  rescue RestClient::ExceptionWithResponse => err
    puts(err.response.to_s)
    begin
      error_response = JSON.parse(err.response.to_s)["error"]
    rescue
      error_response = err.response.to_s
    end
    raise("Rest request failed: #{error_response}")
  rescue StandardError => error
    raise("Rest request failed: #{error.message}")
  end
end

def upload_testsuite_to_browserstack(browserstack_username:, browserstack_access_key:, file_path:)
  puts("Uploading #{file_path} to BrowserStack...")
  response = send_rest_request(
    method: :post,
    url: "https://api-cloud.browserstack.com/app-automate/espresso/v2/test-suite",
    user: browserstack_username,
    password: browserstack_access_key,
    payload: {
      multipart: true,
      file: File.new(file_path, 'rb')
    }
  )

  if !response["test_suite_url"].nil?
    puts("Successfully uploaded xctestrun file to BrowserStack: #{response}")
    return response["test_suite_url"]
  else
    raise("Failed to upload xctestrun file. Response did not contain test_suite_url: #{response}")
  end
end

def run_testsuite_on_browserstack(
  browserstack_username:,
  browserstack_access_key:,
  project:,
  app_url:,
  test_suite_url:,
  devices:,
  build_tag:,
  annotation:
)
  response = send_rest_request(
    method: :post,
    url: "https://api-cloud.browserstack.com/app-automate/espresso/v2/build",
    user: browserstack_username,
    password: browserstack_access_key,
    payload: {
      app: app_url,
      testSuite: test_suite_url,
      devices: devices,
      networkLogs: true,
      deviceLogs: true,
      project: project,
      buildTag: build_tag,
      annotation: annotation
    }
  )

  puts("Successfully triggered tests on BrowserStack: #{response}")
end

class SampleApp
  attr_reader :path, :app_name

  def initialize(firebase_app_id:, path:, google_services_json_b64_var_name:)
    @firebase_app_id = firebase_app_id
    @google_services_json_b64_var_name = google_services_json_b64_var_name
    @path = path
    @app_name = File.basename(path)
  end

   def copy_apks_to_destination(environment:, destination:)
    FileUtils.mkdir_p(destination) if !File.directory?(destination)

    [
      {
        source: "#{@path}/build/outputs/apk/debug/#{@app_name}-debug.apk",
        destination: "#{destination}/#{debug_apk(environment: environment)}"
      },
      {
        source: "#{@path}/build/outputs/apk/androidTest/debug/#{@app_name}-debug-androidTest.apk",
        destination: "#{destination}/#{testsuite_apk(environment: environment)}"
      },
      {
        source: "#{@path}/build/outputs/apk/release/#{@app_name}-release.apk",
        destination: "#{destination}/#{release_apk(environment: environment)}"
      }
    ].each do |file|
      if File.file?(file[:source])
        puts("Copying #{file[:source]} to #{file[:destination]}")
        FileUtils.cp(file[:source], file[:destination])
      else
        puts("#{file[:source]} was not found. Will not be copied.")
      end
    end
  end

  def debug_apk(environment:)
    return "#{@app_name}-#{environment}-debug.apk"
  end

  def firebase_app_id(environment:)
    return @firebase_app_id.fetch(:"#{environment}", nil)
  end

  def prepare_google_services_json(environment:)
    google_services_json_path = "#{@path}/google-services.json"
    if env_var_valid?(@google_services_json_b64_var_name.fetch(:"#{environment}"))
      puts("Creating #{google_services_json_path}")
      IO.write(google_services_json_path, Base64.decode64(ENV[@google_services_json_b64_var_name.fetch(:"#{environment}")]))
    elsif File.file?(google_services_json_path)
      puts("#{google_services_json_path} already exists so won't be created")  
    else
      raise("Error: Environment variable #{@google_services_json_b64_var_name.fetch(:"#{environment}")} is required to create the google-services.json file")
    end
  end

  def release_apk(environment:)
    return "#{@app_name}-#{environment}-release.apk"
  end
 
  def testsuite_apk(environment:)
    return "#{@app_name}-#{environment}-debug-androidTest.apk"
  end
end
