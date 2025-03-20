package com.baidu.aip.asrwakeup3.core.demo.kotlin

/**
 * 资源引用类
 * 用于引用项目中使用的各种资源ID
 */
object R {
    /**
     * 原始资源
     */
    object raw {
        // 语音识别开始提示音
        const val bdspeech_recognition_start = 0
        
        // 语音识别结束提示音
        const val bdspeech_speech_end = 0
        
        // 语音识别成功提示音
        const val bdspeech_recognition_success = 0
        
        // 语音识别错误提示音
        const val bdspeech_recognition_error = 0
        
        // 语音识别取消提示音
        const val bdspeech_recognition_cancel = 0
    }
    
    /**
     * 布局资源
     */
    object layout {
        // 语音识别Activity布局
        const val activity_speech_recognition = 0
    }
    
    /**
     * ID资源
     */
    object id {
        // 语音识别按钮
        const val btn_start_recognition = 0
        
        // 语音识别结果文本框
        const val tv_recognition_result = 0
        
        // 语音识别状态文本框
        const val tv_recognition_status = 0
    }
} 