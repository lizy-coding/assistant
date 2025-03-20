package com.baidu.aip.asrwakeup3.core.demo.kotlin

import com.baidu.aip.asrwakeup3.core.recog.RecogResult

/**
 * 语音识别监听器接口
 * 定义了语音识别过程中的各种回调方法
 */
interface RecognitionListener {
    /**
     * 引擎就绪，可以开始说话
     */
    fun onReady()

    /**
     * 检测到用户开始说话
     */
    fun onBeginningOfSpeech()

    /**
     * 检测到用户结束说话
     */
    fun onEndOfSpeech()

    /**
     * 识别过程中的临时结果
     */
    fun onPartialResult(results: Array<String>?, recogResult: RecogResult?)

    /**
     * 在线语义理解结果
     */
    fun onNluResult(nluResult: String?)

    /**
     * 最终识别结果
     */
    fun onFinalResult(results: Array<String>?, recogResult: RecogResult?)

    /**
     * 识别结束
     */
    fun onFinish(recogResult: RecogResult?)

    /**
     * 识别出错
     */
    fun onError(errorCode: Int, subErrorCode: Int, errorMessage: String?, recogResult: RecogResult?)

    /**
     * 长语音识别结束
     */
    fun onLongFinish()

    /**
     * 音量变化回调
     */
    fun onVolumeChanged(volumePercent: Int, volume: Int)

    /**
     * 原始音频数据回调
     */
    fun onAudioData(data: ByteArray?, offset: Int, length: Int)

    /**
     * 识别引擎退出
     */
    fun onExit()
} 