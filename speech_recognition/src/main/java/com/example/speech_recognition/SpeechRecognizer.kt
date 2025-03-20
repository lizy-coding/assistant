package com.example.speech_recognition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import java.util.Locale

/**
 * 语音识别模块对外暴露的接口
 */
interface SpeechRecognitionCallback {
    fun onRecognitionStart()
    fun onRecognitionResult(text: String)
    fun onRecognitionError(errorCode: Int, errorMessage: String)
    fun onRecognitionEnd()
}

/**
 * 语音识别管理器 - 对外提供统一的语音识别功能
 */
class SpeechRecognitionManager(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var callback: SpeechRecognitionCallback? = null
    private var isListening = false
    
    /**
     * 初始化语音识别器
     */
    fun initialize() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(recognitionListener)
        } else {
            callback?.onRecognitionError(-1, "语音识别功能不可用")
        }
    }
    
    /**
     * 设置回调
     */
    fun setCallback(callback: SpeechRecognitionCallback) {
        this.callback = callback
    }
    
    /**
     * 开始语音识别
     */
    fun startListening(language: Locale = Locale.CHINESE) {
        if (isListening) {
            stopListening()
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        speechRecognizer?.startListening(intent)
        isListening = true
    }
    
    /**
     * 停止语音识别
     */
    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }
    
    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            callback?.onRecognitionStart()
        }
        
        override fun onBeginningOfSpeech() {}
        
        override fun onRmsChanged(rmsdB: Float) {}
        
        override fun onBufferReceived(buffer: ByteArray?) {}
        
        override fun onEndOfSpeech() {
            isListening = false
            callback?.onRecognitionEnd()
        }
        
        override fun onError(error: Int) {
            isListening = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "音频错误"
                SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                SpeechRecognizer.ERROR_NO_MATCH -> "未识别到语音"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙"
                SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
                else -> "未知错误"
            }
            callback?.onRecognitionError(error, errorMessage)
        }
        
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                callback?.onRecognitionResult(matches[0])
            }
        }
        
        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                callback?.onRecognitionResult(matches[0])
            }
        }
        
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}