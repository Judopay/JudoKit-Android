fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android prepare_google_services_json

```sh
[bundle exec] fastlane android prepare_google_services_json
```

Create the google-services.json file for the example app

### android pull_request_checks

```sh
[bundle exec] fastlane android pull_request_checks
```

Run the pull request checks

### android build_example_app

```sh
[bundle exec] fastlane android build_example_app
```

Build the Example App

### android example_app_distribution

```sh
[bundle exec] fastlane android example_app_distribution
```

Deploy a new version of the Example App to the Firebase App Distribution

### android release_sdk

```sh
[bundle exec] fastlane android release_sdk
```

Releases the SDK to Maven Central

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
