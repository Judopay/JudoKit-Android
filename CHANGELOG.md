# judoNative Android SDK change log

## [5.0.1](https://github.com/judopay/Judo-Android/tree/5.0.1) (2016-01-07)

**Implemented enhancements:**
- SHA 256 SSL/TLS Certificate upgrade - an industry-wide security update to protect you against man-in-the-middle attacks
- Maestro card type support is now enabled in the SDK by default.
- The sample app settings page now includes the full list of current and future supported currencies.
- A more useful error message is provided if the judoNative SDK is not properly initialised.

**Bugs fixed:**
- Fixed an issue that prevented card details from being amended when attempting to register a card with validation errors.
- Resolved a crashing issue that would occur when any API error was encountered when performing a transaction, due to ApiError class not being Parcelable when attempting to pass back the Receipt for the transaction.
- Fixed an issue with card digits being skipped when attempting to type into the card number input field.
- Merged judo-sdk and judo-data modules due to an issue with resolving library internal dependencies found when releasing previous SDK version.

## [5.0](https://github.com/judopay/Judo-Android/tree/5.0) (2015-12-10)

**Implemented enhancements:**
- Complete redesign of all screens to use Material Design guidelines - this is a set of guidelines set by Google and the way that most new apps are being designed.
- New design for tablet devices, with payment button in-line.
- The UI of the payment form can now be customized more easily, using Android themes it's possible to override the colours used (e.g. the action bar, background colour, etc.).
- 3D Secure feature implemented and shown when required by the merchant. This will show a web view overlaying the payment form.
- judoShield integration for automatic fraud detection.
- Detection of rooted Android devices, with option to block payments from insecure devices.
- Important change for PCI compliance - SSL settings have been changed to make our SDK compliant and more secure. Due to this change we have had to drop support for older Android versions (prior to Jelly Bean OS, released on phones in 2012).
- SDK is now easier to integrate than ever, due to upgrading the build tools used by the project. Only takes 3 lines of code to bring up a payment screen.
- SDK can now automatically be downloaded from jCenter repository, for one line install of library into app project.
- [Javadocs published to GitHub](http://judopay.github.io/Judo-Android/)

**Bugs fixed:**
- Resolved an issue where token payment/pre-auth could not be performed due to the judo ID was not required when registering cards, this caused a problem when having multiple judo IDs in your account with multiple locations.

**Changed:**
- New class JudoPay, replacing JudoSDKManager for performing initial setup and turning on/off configuration options.
- New class JudoApiService for interacting with JudoPay REST endpoints, using Retrofit 2.0 and rxjava Observable (replaces TransactionQueryApiService, TransactionProcessingApiService, PaymentAction, RegisterAction and TransactionAction).
- Domain classes used with JudoPay REST API moved to judo-data module, new package: com.judopay.model .

**Build changes:**
- SDK now built in Android archive format (.aar).
- SDK file size reduced by 80% (now 215 KB).
- Publish tasks for uploading to bintray.
