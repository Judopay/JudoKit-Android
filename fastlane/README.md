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

### android lint

```sh
[bundle exec] fastlane android lint
```

Lint SDK

### android build

```sh
[bundle exec] fastlane android build
```

Build the SDK

### android test

```sh
[bundle exec] fastlane android test
```

Run SDK Tests

### android publish

```sh
[bundle exec] fastlane android publish
```

Publish the SDK

### android pr_check

```sh
[bundle exec] fastlane android pr_check
```

Run the pull request checks

### android build_sample_apps

```sh
[bundle exec] fastlane android build_sample_apps
```

Build Sample Apps

### android test_sample_apps_firebase

```sh
[bundle exec] fastlane android test_sample_apps_firebase
```

Run instrumented tests for sample apps on Firebase

### android test_sample_apps_browserstack

```sh
[bundle exec] fastlane android test_sample_apps_browserstack
```

Run instrumented tests for sample apps on BrowserStack

### android publish_sample_apps

```sh
[bundle exec] fastlane android publish_sample_apps
```

Publish Sample Apps to Firebase

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
