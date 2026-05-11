# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Prevents crash when using Kotlin reflections on SDK 23
-keep class kotlin.Metadata { *; }

# Gson needs field names intact for JSON serialisation/deserialisation.
# Signature is required so Gson can resolve generic type parameters at runtime.
-keepattributes Signature
-keepattributes *Annotation*

# Keep fields on data classes that Gson serialises (request) or deserialises
# (response) over the network. Without these rules R8 renames the fields and
# the JSON property names no longer match, causing requests/responses to fail.
-keepclassmembers class com.judokit.android.examples.apiclient.CreatePaymentSessionRequest { <fields>; }
-keepclassmembers class com.judokit.android.examples.apiclient.CreatePaymentSessionResponse { <fields>; }
