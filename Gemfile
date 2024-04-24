# frozen_string_literal: true

source 'https://rubygems.org'
git_source(:github) {|repo_name| "https://github.com/#{repo_name}" }

# You may use http://rbenv.org/ or https://rvm.io/ to install and use this version
ruby ">= 2.6.10"

# gem "rails"

gem "fastlane", "~> 2.220"

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)
