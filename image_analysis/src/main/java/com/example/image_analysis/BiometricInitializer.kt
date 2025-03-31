package com.example.image_analysis

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.biometric_auth.api.BiometricAuth

/**
 * 图像分析模块的生物识别初始化器
 */
object BiometricInitializer {
    
    private const val TAG = "BiometricInitializer"
    private const val MODULE_NAME = "image_analysis"
    
    /**
     * 需要进行生物识别验证的方法列表
     */
    private val securedMethods = setOf(
        "takePhoto",
        "analyzeImage",
        "uploadImage",
        "loadFragment",
        "startCamera",
        "openGallery"
    )
    
    /**
     * 初始化生物识别配置
     * 
     * @param context 上下文
     * @return 是否初始化成功
     */
    fun initialize(context: Context): Boolean {
        if (!BiometricAuth.isSupported(context)) {
            Log.w(TAG, "设备不支持生物识别功能")
            return false
        }
        
        // 创建生物识别配置
        val config = BiometricAuth.createConfig(
            title = "图像分析安全验证",
            subtitle = "请验证您的身份以继续操作",
            description = "需要验证您的身份才能进行图像分析相关操作",
            negativeButtonText = "取消",
            securedMethods = securedMethods
        )
        
        // 初始化模块
        BiometricAuth.init(MODULE_NAME, config)
        Log.d(TAG, "图像分析模块生物识别功能初始化成功")
        return true
    }
    
    /**
     * 在执行方法前进行生物识别验证
     * 
     * @param activity 当前活动
     * @param methodName 方法名称
     * @param onSuccess 验证成功回调
     * @param onError 验证失败回调
     */
    fun authenticateBeforeMethod(
        activity: FragmentActivity,
        methodName: String,
        onSuccess: () -> Unit,
        onError: (Int, String) -> Unit = { _, _ -> }
    ) {
        BiometricAuth.verifyMethod(
            activity,
            MODULE_NAME,
            methodName,
            onSuccess,
            onError
        )
    }
} 