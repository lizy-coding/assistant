package com.example.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.assistant.notification.NotificationSettingsScreen

/**
 * 推送通知设置界面
 * 迁移为Compose以符合Jetpack架构，可通过深链或主界面打开
 */
class NotificationSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NotificationSettingsScreen(onNavigateUp = { finish() })
            }
        }
    }
}