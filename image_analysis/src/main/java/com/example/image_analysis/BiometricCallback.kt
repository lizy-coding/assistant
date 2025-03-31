package com.example.image_analysis

/**
 * 生物识别操作回调接口
 * 用于Fragment与宿主Activity通信，处理生物识别相关操作
 */
interface BiometricCallback {
    /**
     * 请求启动相机并进行生物识别验证
     */
    fun requestCameraWithBiometric()
    
    /**
     * 请求分析图片并进行生物识别验证
     * 
     * @param imageUri 要分析的图片URI
     */
    fun requestImageAnalysisWithBiometric(imageUri: android.net.Uri)
    
    /**
     * 请求打开相册并进行生物识别验证
     */
    fun requestGalleryWithBiometric()
} 