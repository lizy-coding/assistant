package com.example.assistant

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            val intent = Intent(this, BiometricSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, BiometricSettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}