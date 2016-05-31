# Judo Android SDK [ ![Download](https://api.bintray.com/packages/judopay/maven/android-sdk/images/download.svg) ](https://bintray.com/judopay/maven/android-sdk/_latestVersion)

The judo Android library lets you integrate secure in-app card payments into your Android app.

Use our UI components for a seamless user experience for card data capture. Minimise your [PCI scope](https://www.pcisecuritystandards.org/pci_security/completing_self_assessment) with a UI that can be themed or customised to match the look and feel of your app.

##### Android Pay has launched in the UK. You can use this SDK to process Android Pay. For more information, please have a look at our [sample app](https://github.com/JudoPay/Judo-AndroidPay-Sample).

##### **\*\*\*Due to industry-wide security updates, versions below 5.0 of this SDK will no longer be supported after 1st Oct 2016. For more information regarding these updates, please read our blog [here](http://hub.judopay.com/pci31-security-updates/).*****

## Getting started
##### 1. Add the library to your project
If you're using Android Studio and Gradle, you can just add the `android-sdk` as a dependency in your app's `build.gradle` file:
```groovy
compile 'com.judopay:android-sdk:5.5'
```
##### 2. Initialize the SDK
From your app's main Activity class, or Application class, initialize the judo SDK with your API token and secret:
```java
Judo judo = new Judo.Builder("<API_TOKEN>", "<API_SECRET>")
    .setEnvironment(Judo.SANDBOX)
    .setJudoId("100915867")
    .setAmount("1.00")
    .setCurrency(Currency.GBP)
    .setConsumerRef("<YOUR REFERENCE>")
    .build();
```
##### 3. Perform a test payment
To show the payment screen, create an Intent for the `PaymentActivity` with the required Intent extras:
```java
Intent intent = new Intent(activity, PaymentActivity.class);
intent.putExtra(Judo.JUDO_OPTIONS, judo);

startActivityForResult(intent, PAYMENT_REQUEST);
```
##### 4. Check the payment result
In the Activity that calls the judo SDK, override the ```Activity.onActivityResult``` method to receive the Receipt from the payment:
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == PAYMENT_REQUEST) {
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt receipt = data.getParcelableExtra(Judo.JUDO_RECEIPT);
                // handle successful payment
        }
    }
}
```

## Next steps
The judo Android library supports a range of customization options. For more information on using judo for Android see our [wiki documentation](https://github.com/JudoPay/Judo-Android/wiki). 

## License
See the [LICENSE](https://github.com/JudoPay/Judo-Android/blob/master/LICENSE) file for license rights and limitations (MIT).
