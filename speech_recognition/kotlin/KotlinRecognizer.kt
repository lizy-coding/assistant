package com.baidu.aip.asrwakeup3.core.demo.kotlin

import android.content.Context
import com.baidu.aip.asrwakeup3.core.util.MyLogger
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import org.json.JSONObject

/**
 * 百度语音识别器Kotlin实现
 * 封装了语音识别核心功能
 */
class KotlinRecognizer(context: Context, listener: RecognitionListener) {
    companion object {
        private const val TAG = "KotlinRecognizer"
        
        // 是否加载离线引擎
        private var isOfflineEngineLoaded = false
        
        // 是否已初始化
        private var isInitialized = false
    }

    // 语音识别事件管理器
    private val eventManager: EventManager
    
    // 事件监听器
    private var eventListener: EventListener
    
    init {
        if (isInitialized) {
            MyLogger.error(TAG, "请先调用release()方法释放资源，再创建新的识别器")
            throw RuntimeException("请先调用release()方法释放资源，再创建新的识别器")
        }
        
        isInitialized = true
        
        // 创建识别监听器适配器
        eventListener = RecognitionListenerAdapter(listener)
        
        // 创建EventManager实例
        eventManager = EventManagerFactory.create(context, "asr")
        
        // 注册事件监听器
        eventManager.registerListener(eventListener)
    }
    
    /**
     * 加载离线引擎
     */
    fun loadOfflineEngine(params: Map<String, Any>) {
        val json = JSONObject(params).toString()
        MyLogger.info("$TAG.Debug", "离线命令词初始化参数：$json")
        
        // 发送加载离线引擎命令
        eventManager.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json, null, 0, 0)
        isOfflineEngineLoaded = true
    }
    
    /**
     * 开始语音识别
     */
    fun start(params: Map<String, Any>) {
        checkInitialized()
        
        // 拼接识别参数
        val json = JSONObject(params).toString()
        MyLogger.info("$TAG.Debug", "识别参数：$json")
        
        // 发送开始识别命令
        eventManager.send(SpeechConstant.ASR_START, json, null, 0, 0)
    }
    
    /**
     * 停止录音
     */
    fun stop() {
        checkInitialized()
        MyLogger.info(TAG, "停止录音")
        
        // 发送停止录音命令
        eventManager.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0)
    }
    
    /**
     * 取消识别
     */
    fun cancel() {
        checkInitialized()
        MyLogger.info(TAG, "取消识别")
        
        // 发送取消识别命令
        eventManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0)
    }
    
    /**
     * 释放资源
     */
    fun release() {
        if (eventManager == null) {
            return
        }
        
        // 取消识别
        cancel()
        
        // 卸载离线引擎
        if (isOfflineEngineLoaded) {
            eventManager.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0)
            isOfflineEngineLoaded = false
        }
        
        // 注销监听器
        eventManager.unregisterListener(eventListener)
        
        isInitialized = false
    }
    
    /**
     * 设置新的监听器
     */
    fun setRecognitionListener(listener: RecognitionListener) {
        checkInitialized()
        
        // 更新监听器
        eventListener = RecognitionListenerAdapter(listener)
        eventManager.registerListener(eventListener)
    }
    
    /**
     * 检查初始化状态
     */
    private fun checkInitialized() {
        if (!isInitialized) {
            throw RuntimeException("识别器已释放，请创建新的识别器实例")
        }
    }
} 