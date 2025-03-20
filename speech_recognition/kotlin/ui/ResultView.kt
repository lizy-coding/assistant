package com.baidu.aip.asrwakeup3.core.demo.kotlin.ui

import android.os.Handler
import android.os.Looper
import android.widget.TextView

/**
 * 语音识别结果显示组件
 * 负责显示语音识别的结果文本
 */
class ResultView(private val textView: TextView) {
    private val handler = Handler(Looper.getMainLooper())
    
    // 是否显示临时结果
    private var showPartialResults = true
    
    /**
     * 设置是否显示临时结果
     */
    fun setShowPartialResults(show: Boolean) {
        this.showPartialResults = show
    }
    
    /**
     * 清空结果
     */
    fun clearResult() {
        handler.post {
            textView.text = ""
        }
    }
    
    /**
     * 添加临时识别结果
     */
    fun addPartialResult(result: String?) {
        if (!showPartialResults || result.isNullOrEmpty()) {
            return
        }
        
        handler.post {
            textView.text = result
        }
    }
    
    /**
     * 添加最终识别结果
     */
    fun addFinalResult(result: String?) {
        if (result.isNullOrEmpty()) {
            return
        }
        
        handler.post {
            textView.text = result
        }
    }
    
    /**
     * 添加错误信息
     */
    fun addErrorResult(errorMessage: String) {
        handler.post {
            textView.text = "【错误】$errorMessage"
        }
    }
    
    /**
     * 添加多行识别结果（长语音）
     */
    fun addMultilineResult(result: String?) {
        if (result.isNullOrEmpty()) {
            return
        }
        
        handler.post {
            val currentText = textView.text.toString()
            if (currentText.isEmpty()) {
                textView.text = result
            } else {
                textView.text = "$currentText\n$result"
            }
        }
    }
} 