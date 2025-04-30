package com.example.assistant

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.biometric_auth.api.BiometricAuth
import com.example.push_notification.api.PushNotificationApi

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 设置ActionBar标题
        supportActionBar?.title = "智能辅助工具"

        // 初始化推送通知模块
        PushNotificationApi.initialize(this)
        
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
        
        // 添加推送通知设置按钮（如果布局中存在）
        findViewById<Button>(R.id.buttonNotificationSettings)?.setOnClickListener {
            openNotificationSettings()
        }
        
        // 电池监控按钮
        findViewById<Button>(R.id.buttonBatteryMonitor)?.setOnClickListener {
            val intent = Intent(this, BatteryActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 打开推送通知设置界面
     */
    private fun openNotificationSettings() {
        // 使用推送通知模块API启动设置界面
        PushNotificationApi.startNotificationSettingsActivity(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                // 打开设置界面
                true
            }
            R.id.menu_notification_settings -> {
                // 打开推送通知设置
                openNotificationSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}