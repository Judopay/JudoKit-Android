# judoNative SDK for Android [ ![Download](https://api.bintray.com/packages/judopay/maven/android-sdk/images/download.svg) ](https://bintray.com/judopay/maven/android-sdk/_latestVersion)

The judoNative Android library lets you integrate secure in-app card payments into your Android app. Judo's SDK enables a faster, simpler and more secure payment experience within your app. 

You can use our out of the box UI for a fully PCI Level 1 compliant payment experience that is customisable to match your app. Alternatively, you can also use the RESTful API directly to implement your own UI.

##### **\*\*\*Due to industry-wide security updates, versions below 5.0 of this SDK will no longer be supported after 1st Oct 2016. For more information regarding these updates, please read our blog [here](http://hub.judopay.com/pci31-security-updates/).*****

## Getting started
##### 1. Add the library to your project
If you're using Android Studio and Gradle, you can just add the android-sdk as a dependency in your app's build.gradle file:
```groovy
compile 'com.judopay:android-sdk:5.1'
```
##### 2. Initialise the SDK
From your app's main Activity class, or Application class, initialise the judo SDK with your API token and secret:
```java
Judo.setup("MY_API_TOKEN", "MY_API_SECRET", Judo.Environment.SANDBOX);
```
##### 3. Perform a test payment
To show the payment screen, create an Intent for the PaymentActivity with the required Intent extras:
```java
Intent intent = new Intent(activity, PaymentActivity.class);
intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
    .setJudoId("35843095834")
    .setAmout("9.99")
    .setCurrency(Currency.GBP)
    .setConsumerRef("consumerRef")
    .build());

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

## Android theming

The judoNative Android SDK provides 3 base Themes for the out-of-the-box UI, which can be used to customize the appearance of the payment form:

  - **Theme.Judo** — Theme with dark background and action bar
  - **Theme.Judo.Light** — Theme with light background and action bar
  - **Theme.Judo.Light.DarkActionBar** — Theme with light background and dark action bar
  
Depending on the styles used in your app, you should expand on the most appropriate theme to ensure a seamless user experience.

![Screenshot of judo light theme](/samples/screens/android-theme-light.png)
![Screenshot of judo light theme](/samples/screens/android-theme-dark.png)
![Screenshot of judo light theme](/samples/screens/android-theme-custom.png)

#### Customizing the theme
1. Create a style that extends from one of the provided base Themes, for example:

    ```xml
    <style name="AppTheme" parent="Theme.Judo.Light">
        <item name="colorPrimary">#3F51B5</item> // action bar colour
        <item name="colorPrimaryDark">#303F9F</item> // status bar colour
        <item name="colorButtonNormal">#E91E63</item> // button colour
        <item name="textColorPrimary">#333333</item> // text colour
        <item name="colorControlActivated">#3F51B5</item> // form field hint colour
    </style>
    ```
2. Specify the activity in your AndroidManifest.xml file with the customized theme:
```xml
   <activity
      android:name="com.judopay.PaymentActivity"
      android:theme="@style/AppTheme"
      tools:replace="android:theme" />
   ```

The full list of Activity classes that can be changed are:

 - com.judopay.PaymentActivity
 - com.judopay.PreAuthActivity
 - com.judopay.RegisterCardActivity
 - com.judopay.TokenPaymentActivity
 - com.judopay.TokenPreAuthActivity

Find more information on how to customize themes on the Android website:
http://developer.android.com/training/material/theme.html#ColorPalette5


## License
See the [LICENSE](https://github.com/JudoPay/Judo-Android/blob/master/LICENSE) file for license rights and limitations (MIT).
