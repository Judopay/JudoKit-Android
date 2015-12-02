# judoNative Android SDK Change Log

## [5.0](https://github.com/judopay/Judo-Android/tree/5.0) (2015-12-03)

**Implemented enhancements:**
- Complete redesign of all screens to use material design guidelines
- Improved payment form layout for tablet devices
- Customizable UI using AppCompat themes to allow payment screen design to be changed
- 3D Secure support when required by merchant bank
- judoShield integration for automatic fraud detection
- Detection of rooted Android devices, with option to block payments from insecure devices
- Security checks to prevent payments from rooted devices
- SSL changes for PCI compliance
- Library available in jCenter repository, for one line install of library into app project
- [Javadocs published to GitHub](http://judopay.github.io/Judo-Android/)

**Changed:**
- New class judoPay, replacing JudoSDKManager for performing initial setup and turning on/off configuration options
- New class JudoApiService for interacting with judoPay REST endpoints, using Retrofit 2.0 and rxjava Observable (replaces TransactionQueryApiService, TransactionProcessingApiService, PaymentAction, RegisterAction and TransactionAction)
- Domain classes used with judoPay REST API moved to judo-data module, new package: com.judopay.model

**Build changes:**
- SDK now built in Android archive format (.aar)
- SDK filesize reduced by 80% (now 192 KB)
- Android OS version support changed to require Android OS Jelly Bean 4.1 or later, due to PCI compliance changes
