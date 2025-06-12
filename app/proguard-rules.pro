# ProGuard rules for RouterManager
#
# These rules keep WebView client implementations and other
# dependencies used by the app when minification is enabled.

# Keep custom WebViewClient and WebChromeClient subclasses
-keep public class * extends android.webkit.WebViewClient {
    public <init>(...);
    public *;
}
-keep public class * extends android.webkit.WebChromeClient {
    public <init>(...);
    public *;
}

# Preserve methods annotated with @JavascriptInterface (none currently,
# but this rule is harmless and future proof).
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Material Components dynamic color support
-keep class com.google.android.material.color.DynamicColors { *; }

# Kotlin coroutines are heavily used; keep their metadata to avoid
# reflection issues during minification.
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# General AndroidX and appcompat rules (safe defaults)
-keep class androidx.lifecycle.** { *; }
-keep class androidx.appcompat.widget.** { *; }

# Optimize by removing logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
