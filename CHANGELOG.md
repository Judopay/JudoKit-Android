# judoNative Android SDK Change Log

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
- SDK can now automatically be downloaded from a repository manager (similar to CocoaPods), for one line install of library into app project.
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
