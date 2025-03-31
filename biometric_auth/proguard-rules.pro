# 保留注解
-keepattributes *Annotation*
-keep @com.example.biometric_auth.BiometricAuthentication class * {*;}
-keepclassmembers class * {
    @com.example.biometric_auth.BiometricAuthentication *;
}

# 保留生物识别相关类
-keep class com.example.biometric_auth.** { *; }
-keep class androidx.biometric.** { *; } 