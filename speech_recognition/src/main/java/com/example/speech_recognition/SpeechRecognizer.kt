//package com.example.speech_recognition
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.speech.RecognitionListener
//import android.speech.RecognizerIntent
//import android.speech.SpeechRecognizer as AndroidSpeechRecognizer
//
///**
// * 系统语音识别实现
// */
//class SystemSpeechRecognizer(private val context: Context) {
//    private var speechRecognizer: AndroidSpeechRecognizer? = null
//    private var callback: SpeechRecognitionCallback? = null
//    private var isListening = false
//
//    /**
//     * 初始化语音识别器
//     */
//    fun initialize() {
//        if (AndroidSpeechRecognizer.isRecognitionAvailable(context)) {
//            speechRecognizer = AndroidSpeechRecognizer.createSpeechRecognizer(context)
//            speechRecognizer?.setRecognitionListener(recognitionListener)
//        } else {
//            callback?.onError(-1, "语音识别功能不可用")
//        }
//    }
//
//    /**
//     * 设置回调
//     */
//    fun setCallback(callback: SpeechRecognitionCallback) {
//        this.callback = callback
//    }
//
//    /**
//     * 开始语音识别
//     */
//    fun startListening() {
//        if (isListening) {
//            stopListening()
//        }
//
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
//            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
//            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
//        }
//
//        speechRecognizer?.startListening(intent)
//        isListening = true
//        callback?.onRecognitionStart()
//    }
//
//    /**
//     * 停止语音识别
//     */
//    fun stopListening() {
//        if (isListening) {
//            speechRecognizer?.stopListening()
//            isListening = false
//        }
//    }
//
//    /**
//     * 释放资源
//     */
//    fun release() {
//        speechRecognizer?.destroy()
//        speechRecognizer = null
//        isListening = false
//    }
//
//    private val recognitionListener = object : RecognitionListener {
//        override fun onReadyForSpeech(params: Bundle?) {}
//
//        override fun onBeginningOfSpeech() {}
//
//        override fun onRmsChanged(rmsdB: Float) {}
//
//        override fun onBufferReceived(buffer: ByteArray?) {}
//
//        override fun onEndOfSpeech() {
//            isListening = false
//            callback?.onRecognitionEnd()
//        }
//
//        override fun onError(error: Int) {
//            isListening = false
//            val errorMessage = when (error) {
//                AndroidSpeechRecognizer.ERROR_AUDIO -> "音频错误"
//                AndroidSpeechRecognizer.ERROR_CLIENT -> "客户端错误"
//                AndroidSpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
//                AndroidSpeechRecognizer.ERROR_NETWORK -> "网络错误"
//                AndroidSpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
//                AndroidSpeechRecognizer.ERROR_NO_MATCH -> "未识别到语音"
//                AndroidSpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙"
//                AndroidSpeechRecognizer.ERROR_SERVER -> "服务器错误"
//                AndroidSpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
//                else -> "未知错误"
//            }
//            callback?.onError(error, errorMessage)
//        }
//
//        override fun onResults(results: Bundle?) {
//            val matches = results?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
//            if (!matches.isNullOrEmpty()) {
//                callback?.onResult(matches[0])
//            }
//        }
//
//        override fun onPartialResults(partialResults: Bundle?) {
//            val partialMatches = partialResults?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
//            if (!partialMatches.isNullOrEmpty()) {
//                callback?.onPartialResult(partialMatches[0])
//            }
//        }
//
//        override fun onEvent(eventType: Int, params: Bundle?) {}
//    }
//}