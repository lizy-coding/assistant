package com.baidu.aip.asrwakeup3.core.demo.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.baidu.aip.asrwakeup3.core.util.MyLogger

/**
 * 百度语音识别Kotlin示例程序入口
 * 
 * 该示例展示了如何使用百度语音SDK进行语音识别
 * 包括在线识别、离线命令词识别和长语音识别等功能
 */
class KotlinSpeechDemo : AppCompatActivity(), View.OnClickListener {
    
    companion object {
        private const val TAG = "KotlinSpeechDemo"
    }
    
    // UI组件
    private lateinit var titleTextView: TextView
    private lateinit var onlineRecognitionButton: Button
    private lateinit var offlineRecognitionButton: Button
    private lateinit var longSpeechRecognitionButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_recognition)
        
        // 初始化UI组件
        initUI()
        
        MyLogger.info(TAG, "示例应用启动成功")
    }
    
    /**
     * 初始化UI组件
     */
    private fun initUI() {
        titleTextView = findViewById(R.id.tv_recognition_status)
        titleTextView.text = "百度语音识别Kotlin示例"
        
        onlineRecognitionButton = findViewById(R.id.btn_start_recognition)
        onlineRecognitionButton.text = "在线语音识别"
        onlineRecognitionButton.setOnClickListener(this)
        
        // 添加新的按钮（实际应用中需要将这些按钮添加到布局文件中）
        offlineRecognitionButton = Button(this)
        offlineRecognitionButton.text = "离线命令词识别"
        offlineRecognitionButton.setOnClickListener(this)
        
        longSpeechRecognitionButton = Button(this)
        longSpeechRecognitionButton.text = "长语音识别"
        longSpeechRecognitionButton.setOnClickListener(this)
    }
    
    /**
     * 按钮点击事件处理
     */
    override fun onClick(view: View) {
        when (view) {
            onlineRecognitionButton -> {
                // 启动在线语音识别
                startSpeechRecognition(RecognitionMode.ONLINE)
            }
            offlineRecognitionButton -> {
                // 启动离线命令词识别
                startSpeechRecognition(RecognitionMode.OFFLINE)
            }
            longSpeechRecognitionButton -> {
                // 启动长语音识别
                startSpeechRecognition(RecognitionMode.LONG_SPEECH)
            }
        }
    }
    
    /**
     * 启动语音识别
     */
    private fun startSpeechRecognition(mode: RecognitionMode) {
        val intent = Intent(this, SpeechRecognitionActivity::class.java).apply {
            putExtra("recognition_mode", mode.name)
        }
        startActivity(intent)
    }
    
    /**
     * 语音识别模式
     */
    enum class RecognitionMode {
        ONLINE,     // 在线语音识别
        OFFLINE,    // 离线命令词识别
        LONG_SPEECH // 长语音识别
    }
} 