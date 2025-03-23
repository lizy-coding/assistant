package com.example.speech_recognition

/**
 * 语音识别回调接口
 */
interface SpeechRecognitionCallback {
    /**
     * 识别开始
     */
    fun onRecognitionStart()
    
    /**
     * 返回部分识别结果
     */
    fun onPartialResult(result: String)
    
    /**
     * 返回最终识别结果
     */
    fun onResult(result: String)
    
    /**
     * 识别错误
     */
    fun onError(errorCode: Int, errorMessage: String)
    
    /**
     * 识别结束
     */
    fun onRecognitionEnd()
} 