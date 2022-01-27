# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class com.judopay.judokit.android.ui.cardverification.components.JsonParsingJavaScriptInterface {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.judopay.judokit.** { *; }
-keepnames class com.judopay.judokit.** { *; }

#-keep class com.judopay.judokit.android.api.model.** { *; }
#-keepnames class com.judopay.judokit.android.api.model.** { *; }
#-keep class com.judopay.judokit.android.model.** { *; }
#-keepnames class com.judopay.judokit.android.model.** { *; }
#-keep class com.judopay.judokit.android.api.error.** { *; }
#-keepnames class com.judopay.judokit.android.api.error.** { *; }