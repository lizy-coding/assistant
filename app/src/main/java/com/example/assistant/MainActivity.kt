package com.example.assistant

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.biometric_auth.api.BiometricAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 设置ActionBar标题
        supportActionBar?.title = "智能辅助工具"

        setupButtons()
    }

    private fun setupButtons() {
        // 语音识别按钮
        findViewById<Button>(R.id.buttonSpeechRecognition).setOnClickListener {
            val intent = Intent(this, SpeechRecognitionActivity::class.java)
            startActivity(intent)
        }

        // 图像分析按钮
        findViewById<Button>(R.id.buttonImageAnalysis).setOnClickListener {
            val intent = Intent(this, ImageAnalysisActivity::class.java)
            startActivity(intent)
        }

        // 生物识别设置按钮
        findViewById<Button>(R.id.buttonBiometricSettings).setOnClickListener {
            // 直接调用生物识别模块API启动指纹管理界面
            BiometricAuth.startFingerprintManager(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                BiometricAuth.startFingerprintManager(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}