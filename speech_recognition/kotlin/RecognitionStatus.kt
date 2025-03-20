package com.baidu.aip.asrwakeup3.core.demo.kotlin

/**
 * 语音识别状态接口
 * 定义了语音识别过程中的各种状态常量
 */
interface RecognitionStatus {
    companion object {
        // 基本状态
        const val STATUS_NONE = 2
        const val STATUS_READY = 3
        const val STATUS_SPEAKING = 4
        const val STATUS_RECOGNITION = 5
        const val STATUS_FINISHED = 6
        const val STATUS_LONG_SPEECH_FINISHED = 7
        const val STATUS_STOPPED = 10

        // 等待状态
        const val STATUS_WAITING_READY = 8001
        const val WHAT_MESSAGE_STATUS = 9001

        // 唤醒状态
        const val STATUS_WAKEUP_SUCCESS = 7001
        const val STATUS_WAKEUP_EXIT = 7003
    }
} 