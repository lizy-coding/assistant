package com.example.biometric_auth.core

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * 生物识别管理器，负责处理生物识别相关操作
 */
class BiometricManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: BiometricManager? = null
        
        fun getInstance(): BiometricManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BiometricManager().also { INSTANCE = it }
            }
        }
        
        // 检查设备是否支持生物识别
        fun isBiometricAvailable(context: Context): Boolean {
            val biometricManager = androidx.biometric.BiometricManager.from(context)
            return when (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> true
                else -> false
            }
        }
    }
    
    /**
     * 显示生物识别认证对话框
     * 
     * @param activity 当前活动
     * @param title 对话框标题
     * @param subtitle 对话框副标题
     * @param description 对话框描述文本
     * @param negativeButtonText 取消按钮文本
     * @param callback 认证结果回调
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String,
        callback: BiometricCallback
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
        
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback.onAuthenticationError(errorCode, errString.toString())
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback.onAuthenticationSucceeded()
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.onAuthenticationFailed()
                }
            })
        
        biometricPrompt.authenticate(promptInfo)
    }
}

/**
 * 生物识别回调接口
 */
interface BiometricCallback {
    fun onAuthenticationSucceeded()
    fun onAuthenticationFailed()
    fun onAuthenticationError(errorCode: Int, errorMessage: String)
} 