package com.baidu.aip.asrwakeup3.core.demo.kotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.baidu.aip.asrwakeup3.core.demo.kotlin.ui.ResultView
import com.baidu.aip.asrwakeup3.core.demo.kotlin.ui.StatusView
import com.baidu.aip.asrwakeup3.core.recog.RecogResult
import com.baidu.aip.asrwakeup3.core.util.MyLogger

/**
 * 语音识别示例Activity
 * 演示如何使用百度语音SDK进行语音识别
 */
class SpeechRecognitionActivity : AppCompatActivity(), RecognitionListener {
    companion object {
        private const val TAG = "SpeechRecognitionDemo"
        
        // 权限请求码
        private const val PERMISSION_REQUEST_CODE = 1
        
        // 所需权限列表
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    
    // 语音识别器
    private var recognizer: KotlinRecognizer? = null
    
    // 语音配置
    private lateinit var config: RecognitionConfig
    
    // UI组件
    private lateinit var startButton: Button
    private lateinit var statusView: StatusView
    private lateinit var resultView: ResultView
    
    // 当前识别状态
    private var currentStatus = RecognitionStatus.STATUS_NONE
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_recognition)
        
        // 初始化UI组件
        initUi()
        
        // 检查并请求权限
        if (checkAndRequestPermissions()) {
            // 初始化语音识别
            initRecognition()
        }
    }
    
    /**
     * 初始化UI组件
     */
    private fun initUi() {
        startButton = findViewById(R.id.btn_start_recognition)
        val statusTextView = findViewById<TextView>(R.id.tv_recognition_status)
        val resultTextView = findViewById<TextView>(R.id.tv_recognition_result)
        
        // 初始化状态和结果显示组件
        statusView = StatusView(this, statusTextView)
        resultView = ResultView(resultTextView)
        
        // 设置按钮点击监听器
        startButton.setOnClickListener { toggleRecognition() }
    }
    
    /**
     * 初始化语音识别
     */
    private fun initRecognition() {
        try {
            // 创建语音识别配置
            config = RecognitionConfig().defaultOnlineConfig()
            
            // 创建语音识别器
            recognizer = KotlinRecognizer(applicationContext, this)
            
            statusView.showMessage("语音识别引擎初始化成功")
            
        } catch (e: Exception) {
            MyLogger.error(TAG, "初始化失败: ${e.message}")
            Toast.makeText(this, "语音识别引擎初始化失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 切换语音识别状态
     */
    private fun toggleRecognition() {
        if (currentStatus == RecognitionStatus.STATUS_NONE || 
            currentStatus == RecognitionStatus.STATUS_FINISHED ||
            currentStatus == RecognitionStatus.STATUS_LONG_SPEECH_FINISHED) {
            // 开始语音识别
            startRecognition()
        } else {
            // 停止语音识别
            stopRecognition()
        }
    }
    
    /**
     * 开始语音识别
     */
    private fun startRecognition() {
        resultView.clearResult()
        statusView.showMessage("正在启动语音识别...")
        startButton.text = "停止识别"
        
        updateRecognitionStatus(RecognitionStatus.STATUS_WAITING_READY)
        
        // 开始语音识别
        recognizer?.start(config.getParams())
    }
    
    /**
     * 停止语音识别
     */
    private fun stopRecognition() {
        statusView.showMessage("正在停止语音识别...")
        startButton.text = "开始识别"
        
        // 停止语音识别
        recognizer?.stop()
        
        updateRecognitionStatus(RecognitionStatus.STATUS_STOPPED)
    }
    
    /**
     * 检查并请求权限
     */
    private fun checkAndRequestPermissions(): Boolean {
        val missingPermissions = mutableListOf<String>()
        
        // 检查所需权限
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        
        // 如果有未授权的权限，请求这些权限
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            return false
        }
        
        return true
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 检查所有权限是否已授权
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 所有权限已授权，初始化语音识别
                initRecognition()
            } else {
                // 权限被拒绝
                Toast.makeText(this, "需要授权相关权限才能使用语音识别功能", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    /**
     * 更新识别状态
     */
    private fun updateRecognitionStatus(status: Int) {
        currentStatus = status
        statusView.updateStatus(status)
        
        // 根据状态更新UI
        when (status) {
            RecognitionStatus.STATUS_NONE,
            RecognitionStatus.STATUS_FINISHED,
            RecognitionStatus.STATUS_LONG_SPEECH_FINISHED,
            RecognitionStatus.STATUS_STOPPED -> {
                startButton.text = "开始识别"
            }
            else -> {
                startButton.text = "停止识别"
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // 释放语音识别资源
        recognizer?.release()
    }
    
    // ------------------------------ RecognitionListener 接口实现 ------------------------------
    
    override fun onReady() {
        updateRecognitionStatus(RecognitionStatus.STATUS_READY)
    }
    
    override fun onBeginningOfSpeech() {
        updateRecognitionStatus(RecognitionStatus.STATUS_SPEAKING)
    }
    
    override fun onEndOfSpeech() {
        updateRecognitionStatus(RecognitionStatus.STATUS_RECOGNITION)
    }
    
    override fun onPartialResult(results: Array<String>?, recogResult: RecogResult?) {
        if (results != null && results.isNotEmpty()) {
            resultView.addPartialResult(results[0])
        }
    }
    
    override fun onNluResult(nluResult: String?) {
        MyLogger.info(TAG, "语义理解结果: $nluResult")
    }
    
    override fun onFinalResult(results: Array<String>?, recogResult: RecogResult?) {
        if (results != null && results.isNotEmpty()) {
            resultView.addFinalResult(results[0])
        }
    }
    
    override fun onFinish(recogResult: RecogResult?) {
        updateRecognitionStatus(RecognitionStatus.STATUS_FINISHED)
    }
    
    override fun onError(errorCode: Int, subErrorCode: Int, errorMessage: String?, recogResult: RecogResult?) {
        // 显示错误信息
        val message = "错误码: $errorCode, 子错误码: $subErrorCode, 错误信息: $errorMessage"
        MyLogger.error(TAG, message)
        statusView.showError(errorCode, errorMessage ?: "未知错误")
        resultView.addErrorResult(errorMessage ?: "识别出错")
        
        // 更新状态
        updateRecognitionStatus(RecognitionStatus.STATUS_NONE)
    }
    
    override fun onLongFinish() {
        updateRecognitionStatus(RecognitionStatus.STATUS_LONG_SPEECH_FINISHED)
    }
    
    override fun onVolumeChanged(volumePercent: Int, volume: Int) {
        // 显示音量
        statusView.updateVolume(volumePercent)
    }
    
    override fun onAudioData(data: ByteArray?, offset: Int, length: Int) {
        // 可以处理原始音频数据
    }
    
    override fun onExit() {
        updateRecognitionStatus(RecognitionStatus.STATUS_NONE)
    }
} 