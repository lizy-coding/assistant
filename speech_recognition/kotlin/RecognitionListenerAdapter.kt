package com.baidu.aip.asrwakeup3.core.demo.kotlin

import com.baidu.aip.asrwakeup3.core.recog.RecogResult
import com.baidu.speech.EventListener
import com.baidu.speech.asr.SpeechConstant
import org.json.JSONException
import org.json.JSONObject

/**
 * 语音识别监听器适配器
 * 将EventListener的回调转换为RecognitionListener接口的方法
 */
class RecognitionListenerAdapter(private val listener: RecognitionListener) : EventListener {

    override fun onEvent(name: String, params: String, data: ByteArray, offset: Int, length: Int) {
        // 音频数据回调
        if (name == SpeechConstant.CALLBACK_EVENT_ASR_AUDIO) {
            listener.onAudioData(data, offset, length)
            return
        }

        // 解析JSON参数
        var result: RecogResult? = null
        if (params.isNotEmpty()) {
            try {
                val jsonObject = JSONObject(params)
                
                // 错误码回调
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_ERROR) {
                    val errorCode = jsonObject.optInt("error")
                    val subErrorCode = jsonObject.optInt("sub_error")
                    val descMessage = jsonObject.optString("desc", "")
                    listener.onError(errorCode, subErrorCode, descMessage, result)
                    return
                }
                
                // 状态回调
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_READY) {
                    listener.onReady()
                    return
                }
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_BEGIN) {
                    listener.onBeginningOfSpeech()
                    return
                }
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_END) {
                    listener.onEndOfSpeech()
                    return
                }
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_EXIT) {
                    listener.onExit()
                    return
                }
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_VOLUME) {
                    val volumePercent = jsonObject.optInt("volume_percent", 0)
                    val volume = jsonObject.optInt("volume", 0)
                    listener.onVolumeChanged(volumePercent, volume)
                    return
                }
                
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL) {
                    result = RecogResult.parseJson(params)
                    val resultType = result.resultType
                    if (resultType == "partial_result") {
                        val results = result.results
                        listener.onPartialResult(results, result)
                    } else if (resultType == "nlu_result") {
                        val nluResult = result.origalJson
                        listener.onNluResult(nluResult)
                    } else if (resultType == "final_result") {
                        val results = result.results
                        listener.onFinalResult(results, result)
                    }
                    return
                }
                
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_FINISH) {
                    // 识别结束
                    result = RecogResult.parseJson(params)
                    if (result.hasError()) {
                        val errorCode = result.error
                        val subErrorCode = result.subError
                        val descMessage = result.desc
                        listener.onError(errorCode, subErrorCode, descMessage, result)
                    } else {
                        listener.onFinish(result)
                    }
                    return
                }
                
                if (name == SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH) {
                    listener.onLongFinish()
                    return
                }
                
            } catch (e: JSONException) {
                // 如果JSON解析出错，忽略
            }
        }
    }
} 