# ==============================================================================
# Debugging — keep source file & line numbers for legible crash reports
# ==============================================================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile


# ==============================================================================
# Kotlin
# ==============================================================================
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }


# ==============================================================================
# Gson + Retrofit — JSON deserialisation via reflection
# Without these rules R8 renames field names and Gson can no longer map JSON.
# ==============================================================================
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Gson internals
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep every class whose members carry @SerializedName (DTOs)
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Also keep the DTO classes themselves so Gson can instantiate them
-keep class com.joystick.app.data.remote.dto.** { *; }


# ==============================================================================
# Retrofit
# ==============================================================================
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# Keep our API service interface
-keep interface com.joystick.app.data.remote.api.** { *; }


# ==============================================================================
# OkHttp / Okio
# ==============================================================================
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**


# ==============================================================================
# Hilt / Dagger — keep generated component & entry-point classes
# ==============================================================================
-keep class dagger.** { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-dontwarn dagger.**
-dontwarn dagger.hilt.**


# ==============================================================================
# Room — entity, DAO and database classes use reflection
# ==============================================================================
-keep @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }
-dontwarn androidx.room.**


# ==============================================================================
# Kotlin Serialization — @Serializable classes used for Nav3 type-safe routes
# ==============================================================================
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.joystick.app.**$$serializer { *; }
-keepclassmembers class com.joystick.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.joystick.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * { *; }
-dontwarn kotlinx.serialization.**

# Keep our navigation routes explicitly
-keep class com.joystick.app.navigation.** { *; }


# ==============================================================================
# Jetpack Navigation 3 (Nav3)
# ==============================================================================
-keep class androidx.navigation3.** { *; }
-dontwarn androidx.navigation3.**


# ==============================================================================
# Kotlin Coroutines
# ==============================================================================
-dontwarn kotlinx.coroutines.**
# Fix: R8 removes volatile fields used by coroutine internal state machines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}


# ==============================================================================
# Coil — image loading
# ==============================================================================
-keep class coil.** { *; }
-dontwarn coil.**


# ==============================================================================
# AndroidX Media3 / ExoPlayer
# Renderers and decoders are instantiated by fully-qualified name via reflection
# ==============================================================================
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
-keepclassmembers class androidx.media3.exoplayer.** implements androidx.media3.exoplayer.Renderer {
    <init>(...);
}


# ==============================================================================
# AndroidX Lifecycle / Compose
# ==============================================================================
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**
-dontwarn androidx.compose.**
