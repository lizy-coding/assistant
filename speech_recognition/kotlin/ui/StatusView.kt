package com.baidu.aip.asrwakeup3.core.demo.kotlin.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.baidu.aip.asrwakeup3.core.demo.kotlin.RecognitionStatus

/**
 * 语音识别状态显示组件
 * 负责显示语音识别过程中的各种状态信息
 */
class StatusView(private val context: Context, private val textView: TextView) {
    
    private val handler = Handler(Looper.getMainLooper())
    
    /**
     * 更新状态
     */
    fun updateStatus(status: Int) {
        handler.post {
            when (status) {
                RecognitionStatus.STATUS_NONE -> 
                    textView.text = "初始状态"
                
                RecognitionStatus.STATUS_READY -> 
                    textView.text = "准备就绪，可以开始说话"
                
                RecognitionStatus.STATUS_SPEAKING -> 
                    textView.text = "正在聆听..."
                
                RecognitionStatus.STATUS_RECOGNITION -> 
                    textView.text = "正在识别..."
                
                RecognitionStatus.STATUS_FINISHED -> 
                    textView.text = "识别完成"
                
                RecognitionStatus.STATUS_LONG_SPEECH_FINISHED -> 
                    textView.text = "长语音识别完成"
                
                RecognitionStatus.STATUS_STOPPED -> 
                    textView.text = "识别已停止"
                
                RecognitionStatus.STATUS_WAITING_READY -> 
                    textView.text = "等待引擎准备..."
                
                RecognitionStatus.STATUS_WAKEUP_SUCCESS -> 
                    textView.text = "唤醒成功"
                
                RecognitionStatus.STATUS_WAKEUP_EXIT -> 
                    textView.text = "唤醒退出"
                
                else -> 
                    textView.text = "未知状态: $status"
            }
        }
    }
    
    /**
     * 显示音量大小
     */
    fun updateVolume(volumePercent: Int) {
        handler.post {
            textView.text = "音量: $volumePercent%"
        }
    }
    
    /**
     * 显示错误信息
     */
    fun showError(errorCode: Int, errorMessage: String) {
        handler.post {
            textView.text = "错误: $errorCode - $errorMessage"
        }
    }
    
    /**
     * 显示自定义消息
     */
    fun showMessage(message: String) {
        handler.post {
            textView.text = message
        }
    }
} 