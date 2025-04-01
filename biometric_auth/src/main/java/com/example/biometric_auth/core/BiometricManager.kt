package com.example.biometric_auth.core

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.biometric_auth.storage.FingerprintDataStore

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
                    // 验证指纹数据是否在存储中
                    val cryptoObject = result.cryptoObject
                    val fingerprintHash = generateFingerprintHash(cryptoObject)
                    
                    // 如果没有存储指纹数据，则默认允许通过
                    val fingerprintStore = FingerprintDataStore.getInstance(activity)
                    if (fingerprintStore.getFingerprintCount() == 0 || fingerprintStore.verifyFingerprint(fingerprintHash)) {
                        callback.onAuthenticationSucceeded()
                    } else {
                        // 指纹存在但不匹配
                        callback.onAuthenticationFailed()
                    }
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.onAuthenticationFailed()
                }
            })
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    /**
     * 生成指纹哈希值
     * 为简化实现，此处采用模拟方法
     */
    private fun generateFingerprintHash(cryptoObject: BiometricPrompt.CryptoObject?): String {
        // 实际应用中，应根据cryptoObject生成唯一的指纹哈希
        // 这里为简化处理，返回系统ID + 设备信息的哈希值
        return "biometric_hash_" + System.currentTimeMillis().toString()
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