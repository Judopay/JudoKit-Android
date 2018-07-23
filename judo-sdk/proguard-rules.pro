# Retrofit
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn sun.misc.**
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient


# RxJava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}

# AppCompat v7 & Design
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Judopay classes
-keep interface com.judopay.JudoApiService { *; }
-keep interface com.judopay.cardverification.WebViewListener { *; }
-keep interface com.judopay.view.CardNumberEntryView$ScanCardButtonListener { *; }
-keep public interface com.judopay.ProgressListener { *; }
-keep public interface com.judopay.cardverification.AuthorizationListener { *; }
-keep public interface com.judopay.view.PasteListener { *; }
-keep class com.judopay.model.** { *; }
-keep public class com.judopay.model.* { *; }
-keep public class com.judopay.error.* { *; }
-keep public final class com.judopay.model.* { *; }
-keep class com.judopay.Judo { *; }
-keep class com.judopay.Judo$* { *; }
-keep class com.judopay.api.Tls12SslSocketFactory { *; }
-keep class com.judopay.api.JudoApiServiceFactory { *; }
-keep class com.judopay.PaymentActivity { public *; }
-keep class com.judopay.PaymentFragment { public *; }
-keep class com.judopay.PreAuthActivity { public *; }
-keep class com.judopay.PreAuthFragment { public *; }
-keep class com.judopay.RegisterCardActivity { public *; }
-keep class com.judopay.RegisterCardFragment { public *; }
-keep class com.judopay.api.ApiError { *; }
-keep class com.judopay.api.ApiError$* { *; }
-keep class com.judopay.validation.Validation { *; }
