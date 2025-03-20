package com.example.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assistant.R
import com.example.speech_recognition.SpeechRecognitionCallback
import com.example.speech_recognition.SpeechRecognitionManager

class SpeechRecognitionActivity : AppCompatActivity(), SpeechRecognitionCallback {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var statusTextView: TextView
    
    private lateinit var speechRecognitionManager: SpeechRecognitionManager
    
    private val RECORD_AUDIO_PERMISSION_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_recognition)
        
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        resultTextView = findViewById(R.id.result_text_view)
        statusTextView = findViewById(R.id.status_text_view)
        
        // 初始化语音识别模块
        speechRecognitionManager = SpeechRecognitionManager(this)
        speechRecognitionManager.setCallback(this)
        speechRecognitionManager.initialize()
        
        startButton.setOnClickListener {
            if (checkPermission()) {
                speechRecognitionManager.startListening()
            } else {
                requestPermission()
            }
        }
        
        stopButton.setOnClickListener {
            speechRecognitionManager.stopListening()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognitionManager.release()
    }
    
    override fun onRecognitionStart() {
        runOnUiThread {
            statusTextView.text = "正在听取..."
            startButton.isEnabled = false
            stopButton.isEnabled = true
        }
    }
    
    override fun onRecognitionResult(text: String) {
        runOnUiThread {
            resultTextView.text = text
        }
    }
    
    override fun onRecognitionError(errorCode: Int, errorMessage: String) {
        runOnUiThread {
            statusTextView.text = "错误: $errorMessage"
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }
    
    override fun onRecognitionEnd() {
        runOnUiThread {
            statusTextView.text = "识别结束"
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }
    
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speechRecognitionManager.startListening()
            } else {
                Toast.makeText(this, "需要录音权限才能使用语音识别功能", Toast.LENGTH_SHORT).show()
            }
        }
    }
}