-keep class com.judopay.judokit.** { *; }
-keepnames class com.judopay.judokit.** { *; }

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
# https://raw.githubusercontent.com/square/okhttp/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Nimbus issues with R8 full mode
# https://bitbucket.org/connect2id/nimbus-jose-jwt/issues/518/using-r8-full-mode-causes-issues
-dontwarn net.jcip.annotations.Immutable
-dontwarn net.jcip.annotations.ThreadSafe
-keep class com.nimbusds.jose.shaded.gson.reflect.TypeToken { *; }
-keep class * extends com.nimbusds.jose.shaded.gson.reflect.TypeToken
-dontwarn com.google.crypto.tink.subtle.*
