package com.example.assistant

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.speech_recognition.SpeechRecognitionDemoFragment

class SpeechRecognitionActivity : AppCompatActivity() {
    
    private val TAG = "SpeechRecognitionActivity"
    
    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_recognition)
        
        Log.d(TAG, "创建语音识别Activity")
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SpeechRecognitionDemoFragment())
                .commit()
            
            Log.d(TAG, "加载语音识别Fragment")
        }
    }
    
    @SuppressLint("LongLogTag")
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "销毁语音识别Activity")
    }
} 