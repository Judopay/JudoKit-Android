# judoNative Android SDK change log

## [5.2](https://github.com/judopay/Judo-Android/tree/5.2) (TBC)
 
**Implemented enhancements:**
 - Card helper images now animate when a change in card type is detected.
 - Fragment transaction callbacks - it's now possible to handle the result from a transaction when using the provided Fragments on the SDK. This allows for more customization around what gets shown when a transaction is successful, declined or an error occurs. If calling an Activity directly and using the ```Activity.onActivityResult()``` method as a callback, this will remain as before.

**Changes:**
 - Support for passing Activity Intent extras using the constants defined in ```Judo``` has been removed, in favor of using the ```JudoOptions``` builder for more easily passing data to an Activity.
 - Style updates to payment card entry form to match Google Material Design guidelines.
 - judo API token and secret can now be specified directly in the Android Manifest instead of programmatically.
 - Client integration method reporting data is now sent with the transaction to indicate if a custom UI or judo provided UI is used.
 
**Bugs fixed:**
- The start date and issue number fields were shown when making a token payment when not needed.
- Billing postcode now longer goes into full screen entry mode when focusing in landscape mode.
- A crash was occuring when inputting an Amex card number longer than 15 digits, this has now been fixed.

## [5.1.2](https://github.com/judopay/Judo-Android/tree/5.1.2) (2016-02-29)

**Bugs fixed:**
 - VoidRequest, CollectionRequest and RefundRequest no longer accept a 'yourPaymentReference' constructor argument as the request can be detected as a duplicate if the same payment reference was used from the pre-auth or payment request.

**Changes:**
- When performing a token payment, the expiry date of the token card is now shown along with the card number last 4 digits.
- Request classes renamed: PaymentRequest, TokenRequest, RegisterCardRequest, VoidRequest, RefundRequest, CollectionRequest.
- CV2 card location icon now shown on launch of payment screen, previously was only shown after card number input.

## [5.1.1](https://github.com/judopay/Judo-Android/tree/5.1.1) (2016-02-11)

**Implemented enhancements:**
- Added support for voiding pre-auth transactions to ```JudoApiService```

**Bugs fixed:**
- Visa Credit, Visa Debit and Visa Electron now show Visa logo when performing a token payment or pre-auth
- Fixed an issue where additional API requests would fail after an initial transaction request was performed.
- Resolved an issue where making a token payment/pre-auth would not allow a 4 digit CIDV to be entered for an Amex card.
- Resolved an issue where an incorrect error message was given if the required Activity Extras was not passed to a ```JudoActivity```.
- Fixed an issue where the transaction metadata was not sent for register card transactions.
- Fixed an issue where the incorrect dialog message was shown when a card was declined during a card registration.

**Changes:**
- When a duplicated transaction is detected, a ```DuplicateTransactionException``` is now thrown instead of the original transaction response being replayed.
- UI tests moved to judo-sdk module and test framework upgraded to Espresso 2.
- Retrofit library upgraded to latest version.
- New method for obtaining ```JudoApiService``` instance, call ```Judo.getApiService(context);```
- ```JudoApiService``` method for completing a transaction with 3D Secure verification renamed.
- Address Verification Service (AVS) postcode field can now be skipped if card holder is outside UK, USA and Canada.  
- Removed the ability to pass pre-filled card data into the card entry form when performing a token payment/pre-auth

## [5.1](https://github.com/judopay/Judo-Android/tree/5.1) (2016-01-28)

**Implemented enhancements:**
- Brazilian Real added to list of supported currencies.
- Enable 3D Secure verification for all transactions by default, if required by the Merchant's bank.
- Allow transactions for Android devices with root permissions, with option to block if required.
- New Android views provided for card data entry for use when writing a custom UI.
- Display secure server transmission text in the payment form, to indicate transaction is performed securely.

**Bugs fixed:**
- Fixed a crashing issue that could occur if an Activity was restarted due to low memory.
- Resolved an issue where the postcode field hint would not render correctly when switching between countries.
- Fixed an issue where the postcode field would not pick up the theme's tint color on pre-Lollipop devices.
- Visa Electron/Visa Debit now correctly detected as a Visa card during card number entry.

**Changes:**
- New ```JudoOptions``` class for sending type safe data between Activity and Fragment instances.
- New fields included in Transaction classes for setting mobile number and email address.

## [5.0.1](https://github.com/judopay/Judo-Android/tree/5.0.1) (2016-01-12)

**Implemented enhancements:**
- SHA 256 SSL/TLS Certificate upgrade - an industry-wide security update to protect you against man-in-the-middle attacks
- Maestro card type support is now enabled in the SDK by default.
- The sample app settings page now includes the full list of current and future supported currencies.
- A more useful error message is provided if the judoNative SDK is not properly initialized.
- Duplication prevention built in to protect merchants and consumers against duplicated transactions via unique payment reference.

**Bugs fixed:**
- Fixed an issue that prevented card details from being amended when attempting to register a card with validation errors.
- Resolved a crashing issue that would occur when any API error was encountered when performing a transaction, due to ApiError class not being Parcelable when attempting to pass back the Receipt for the transaction.
- Fixed an issue with card digits being skipped when attempting to type into the card number input field.
- Merged judo-sdk and judo-data modules due to an issue with resolving library internal dependencies found when releasing previous SDK version.

**Changes:**
- Renamed JudoPay initialization class to be called Judo, for consistency across SDK platforms.

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
