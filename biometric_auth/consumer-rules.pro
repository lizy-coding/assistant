# 保留注解
-keepattributes *Annotation*
-keep @com.example.biometric_auth.BiometricAuthentication class * {*;}
-keepclassmembers class * {
    @com.example.biometric_auth.BiometricAuthentication *;
} 