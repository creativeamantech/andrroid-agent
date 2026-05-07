# Keep core logic for AI agent to prevent Play Protect flags for over-obfuscation
# or unrecognized runtime loading patterns.
-keep class com.mahavtaar.droidagent.core.** { *; }
-keep class com.mahavtaar.droidagent.data.** { *; }

# Keep Dagger/Hilt components
-keep class dagger.** { *; }
-keep class * extends dagger.internal.Factory
-keep class * implements dagger.internal.Factory

# Keep Room components
-keep class androidx.room.** { *; }

# Keep Compose and UI entries
-keep class com.mahavtaar.droidagent.ui.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Ignore common warnings for missing libraries
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn kotlinx.serialization.**
-dontwarn okhttp3.**
-dontwarn org.jsoup.**
-dontwarn javax.lang.model.**
-dontwarn org.tensorflow.lite.gpu.GpuDelegateFactory**
-dontwarn javax.lang.model.**
-dontwarn org.tensorflow.lite.gpu.GpuDelegateFactory**
