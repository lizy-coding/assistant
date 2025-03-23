package com.example.assistant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 语音识别入口按钮
        val speechRecognitionButton = findViewById<Button>(R.id.btn_speech_recognition)
        speechRecognitionButton.setOnClickListener {
            Log.d(TAG, "点击语音识别入口")
            // 启动语音识别Activity
            val intent = Intent(this, SpeechRecognitionActivity::class.java)
            startActivity(intent)
        }

        // 图像分析入口按钮
        val imageAnalysisButton = findViewById<Button>(R.id.btn_image_analysis)
        imageAnalysisButton.setOnClickListener {
            Log.d(TAG, "点击图像分析入口")
            // 修改为直接启动本地 ImageAnalysisActivity
            val intent = Intent(this, ImageAnalysisActivity::class.java)
            startActivity(intent)
        }
    }
}