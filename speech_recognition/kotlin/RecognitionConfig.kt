package com.baidu.aip.asrwakeup3.core.demo.kotlin

import com.baidu.speech.asr.SpeechConstant

/**
 * 语音识别配置类
 * 用于构建和管理语音识别参数
 */
class RecognitionConfig {
    // 参数Map
    private val params = mutableMapOf<String, Any>()
    
    init {
        // 添加默认的授权信息
        params.putAll(AuthManager.getAuthParams())
    }
    
    /**
     * 设置识别模型类型
     * @param modelType ASR_MODEL_TYPE_SEARCH 搜索模型
     *                  ASR_MODEL_TYPE_INPUT 输入模型
     */
    fun setAsrModelType(modelType: String): RecognitionConfig {
        params[SpeechConstant.ASR_MODEL_TYPE] = modelType
        return this
    }
    
    /**
     * 设置在线语言模型
     * @param language zh-CN, 中文
     *                 en-US, 英文
     */
    fun setLanguage(language: String): RecognitionConfig {
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = true
        params[SpeechConstant.ACCEPT_AUDIO_DATA] = true
        params[SpeechConstant.SOUND_START] = R.raw.bdspeech_recognition_start
        params[SpeechConstant.SOUND_END] = R.raw.bdspeech_speech_end
        params[SpeechConstant.SOUND_SUCCESS] = R.raw.bdspeech_recognition_success
        params[SpeechConstant.SOUND_ERROR] = R.raw.bdspeech_recognition_error
        params[SpeechConstant.SOUND_CANCEL] = R.raw.bdspeech_recognition_cancel
        params[SpeechConstant.ACCEPT_LANGUAGE] = language
        return this
    }
    
    /**
     * 设置采样率
     * @param rate 采样率，16000或8000
     */
    fun setSampleRate(rate: Int): RecognitionConfig {
        params[SpeechConstant.SAMPLE_RATE] = rate
        return this
    }
    
    /**
     * 设置是否开启语义理解
     * @param enable true开启，false关闭
     */
    fun enableNlu(enable: Boolean): RecognitionConfig {
        params[SpeechConstant.ACCEPT_NLU] = enable
        return this
    }
    
    /**
     * 设置是否开启标点符号
     * @param enable true开启，false关闭
     */
    fun enablePunctuation(enable: Boolean): RecognitionConfig {
        params[SpeechConstant.ENABLE_PUNCTUATION] = enable
        return this
    }
    
    /**
     * 设置是否开启长语音
     * @param enable true开启，false关闭
     */
    fun enableLongSpeech(enable: Boolean): RecognitionConfig {
        params[SpeechConstant.ASR_ENABLE_LONG_SPEECH] = enable
        return this
    }
    
    /**
     * 设置VAD切分的语音前端点
     * @param ms 语音前端点，单位毫秒
     */
    fun setVadHeadSilenceMs(ms: Int): RecognitionConfig {
        params[SpeechConstant.VAD_HEAD_SILENCE_MS] = ms
        return this
    }
    
    /**
     * 设置VAD切分的语音后端点
     * @param ms 语音后端点，单位毫秒
     */
    fun setVadTailSilenceMs(ms: Int): RecognitionConfig {
        params[SpeechConstant.VAD_TAIL_SILENCE_MS] = ms
        return this
    }
    
    /**
     * 设置最长语音时长
     * @param seconds 最长语音时长，单位秒
     */
    fun setMaxSpeechLength(seconds: Int): RecognitionConfig {
        params[SpeechConstant.MAX_SPEECH_LENGTH] = seconds * 1000
        return this
    }
    
    /**
     * 设置离线命令词支持
     * @param slot 命令词槽
     */
    fun setOfflineSlot(slot: String): RecognitionConfig {
        params[SpeechConstant.SLOT_DATA] = slot
        return this
    }
    
    /**
     * 设置自定义参数
     * @param key 参数名
     * @param value 参数值
     */
    fun setParam(key: String, value: Any): RecognitionConfig {
        params[key] = value
        return this
    }
    
    /**
     * 获取所有参数
     * @return 参数Map
     */
    fun getParams(): Map<String, Any> {
        return params
    }
    
    /**
     * 默认在线识别设置
     */
    fun defaultOnlineConfig(): RecognitionConfig {
        setAsrModelType(SpeechConstant.ASR_MODEL_TYPE_GENERAL)
        setSampleRate(16000)
        setLanguage("zh-CN")
        enableNlu(true)
        enablePunctuation(true)
        setVadHeadSilenceMs(300)
        setVadTailSilenceMs(800)
        setMaxSpeechLength(60)
        return this
    }
    
    /**
     * 默认离线识别设置
     */
    fun defaultOfflineConfig(slot: String): RecognitionConfig {
        setAsrModelType(SpeechConstant.ASR_MODEL_TYPE_LOCAL)
        setSampleRate(16000)
        setOfflineSlot(slot)
        return this
    }
    
    /**
     * 默认长语音识别设置
     */
    fun defaultLongSpeechConfig(): RecognitionConfig {
        defaultOnlineConfig()
        enableLongSpeech(true)
        return this
    }
} 