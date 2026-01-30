# ============================================================================
# JudoKit-Android SDK - Consumer ProGuard/R8 Rules
# ============================================================================
# These rules are automatically included when consumers use this library.
# They ensure proper functionality when the consuming app enables minification.
# ============================================================================

# ----------------------------------------------------------------------------
# Kotlin Metadata
# ----------------------------------------------------------------------------
# Required for Kotlin reflection and proper class handling
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# ----------------------------------------------------------------------------
# Public API - Entry Points
# ----------------------------------------------------------------------------
# Main SDK entry point and configuration
-keep class com.judopay.judokit.android.Judo { *; }
-keep class com.judopay.judokit.android.Judo$Builder { *; }
-keep class com.judopay.judokit.android.JudoActivity { *; }

# API Service interfaces (Retrofit)
-keep interface com.judopay.judokit.android.api.JudoApiService { *; }
-keep interface com.judopay.judokit.android.api.RecommendationApiService { *; }
-keep class com.judopay.judokit.android.api.factory.JudoApiServiceFactory { *; }

# ----------------------------------------------------------------------------
# Model Classes - Gson Serialization
# ----------------------------------------------------------------------------
# Keep all model classes used for JSON serialization/deserialization
# These classes use @SerializedName annotations and Gson reflection

# Request models
-keep class com.judopay.judokit.android.api.model.request.** { *; }
-keep class com.judopay.judokit.android.api.model.request.threedsecure.** { *; }
-keep class com.judopay.judokit.android.api.model.request.recommendation.** { *; }

# Response models
-keep class com.judopay.judokit.android.api.model.response.** { *; }

# UI model classes
-keep class com.judopay.judokit.android.ui.cardentry.model.Country { *; }
-keep class com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions { *; }

# Authorization models
-keep class com.judopay.judokit.android.api.model.BasicAuthorization { *; }
-keep class com.judopay.judokit.android.api.model.PaymentSessionAuthorization { *; }
-keep class com.judopay.judokit.android.api.model.Authorization { *; }

# Error models
-keep class com.judopay.judokit.android.api.error.** { *; }

# Core domain models (Parcelable and configuration)
-keep class com.judopay.judokit.android.model.** { *; }

# Google Pay models
-keep class com.judopay.judokit.android.model.googlepay.** { *; }

# ----------------------------------------------------------------------------
# Gson Type Adapters & Deserializers
# ----------------------------------------------------------------------------
-keep class com.judopay.judokit.android.api.deserializer.** { *; }

# Keep Gson @SerializedName annotations
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Prevent R8 from stripping interface information needed for Gson
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# ----------------------------------------------------------------------------
# Retrofit
# ----------------------------------------------------------------------------
# Keep generic signature of Call, Response (R8 full mode strips signatures)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep service method parameter and return types
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ----------------------------------------------------------------------------
# Room Database
# ----------------------------------------------------------------------------
-keep class com.judopay.judokit.android.db.JudoRoomDatabase { *; }
-keep class com.judopay.judokit.android.db.dao.** { *; }
-keep class com.judopay.judokit.android.db.entity.** { *; }
-keep class com.judopay.judokit.android.db.JudoTypeConverters { *; }

# ----------------------------------------------------------------------------
# Enums
# ----------------------------------------------------------------------------
# Keep enum values and valueOf methods for serialization
-keepclassmembers enum com.judopay.judokit.android.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    <fields>;
}

# ----------------------------------------------------------------------------
# Parcelable
# ----------------------------------------------------------------------------
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ----------------------------------------------------------------------------
# Sealed Classes
# ----------------------------------------------------------------------------
# Keep sealed class hierarchies for proper type checking
-keep class com.judopay.judokit.android.api.model.response.JudoApiCallResult { *; }
-keep class com.judopay.judokit.android.api.model.response.JudoApiCallResult$* { *; }
-keep class com.judopay.judokit.android.model.JudoPaymentResult { *; }
-keep class com.judopay.judokit.android.model.JudoPaymentResult$* { *; }
-keep class com.judopay.judokit.android.model.SubProductInfo { *; }
-keep class com.judopay.judokit.android.model.SubProductInfo$* { *; }

# ----------------------------------------------------------------------------
# Third-Party Dependencies
# ----------------------------------------------------------------------------
# Ravelin (optional dependency)
-dontwarn com.ravelin.**

# ----------------------------------------------------------------------------
# Miscellaneous
# ----------------------------------------------------------------------------
# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

## Keep annotations
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
