package com.baidu.aip.asrwakeup3.core.demo.kotlin

import com.baidu.speech.asr.SpeechConstant
import java.util.LinkedHashMap

/**
 * 百度语音SDK认证管理类
 * 注意：API密钥信息敏感，建议在实际应用中从服务器获取或加密存储
 */
object AuthManager {
    /**
     * 获取API Key
     */
    fun getApiKey(): String {
        // TODO: 替换为您的API Key
        return "请替换为您的API Key"
    }

    /**
     * 获取Secret Key
     */
    fun getSecretKey(): String {
        // TODO: 替换为您的Secret Key
        return "请替换为您的Secret Key"
    }

    /**
     * 获取应用ID
     */
    fun getAppId(): String {
        // TODO: 替换为您的App ID
        return "请替换为您的App ID"
    }

    /**
     * 获取认证参数
     */
    fun getAuthParams(): Map<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[SpeechConstant.APP_ID] = getAppId()
        params[SpeechConstant.APP_KEY] = getApiKey()
        params[SpeechConstant.SECRET] = getSecretKey()
        return params
    }
} 