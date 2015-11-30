# Change Log

## [5.0](https://github.com/judopay/Judo-Android/tree/5.0) (2015-12-03)

**Implemented enhancements:**
- Complete redesign of all screens to use Material Design guidelines
- Improved payment form layout for tablet devices
- Customisable UI using AppCompat themes to allow payment screen design to be changed.
- 3D-Secure support when required by merchant bank
- JudoShield integration for automatic fraud detection
- Detection of Rooted Android device, with option to block payments from insecure devices.
- Security checks to prevent payments from rooted devices
- SSL changes for PCI-Compliance
- Library available in jCenter repository, for one line install of library into app project.
- [Javadocs published to GitHub](http://judopay.github.io/Judo-Android/)

**Changed:**
- New class JudoApiService for interacting with JudoPay REST endpoints, using Retrofit 2.0 and rxjava Observable (replaces TransactionQueryApiService, TransactionProcessingApiService, PaymentAction, RegisterAction and TransactionAction).
- Domain classes used with JudoPay REST API moved to judo-data module, new package: com.judopay.model

**Build changes:**
- SDK now built in Android archive format (.aar)
- SDK filesize reduced by 80% (now 192 KB)
- Android OS Version support changed to require Android OS Jelly Bean 4.1 or later, due to PCI-Compliance changes.
