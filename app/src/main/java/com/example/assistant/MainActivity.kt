package com.example.assistant

import android.os.Bundle
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.compose.material3.MaterialTheme
import com.example.assistant.ui.AssistantApp
import com.example.biometric_auth.api.BiometricAuth
import com.example.push_notification.api.PushNotificationApi

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化推送通知模块，确保全局可用
        PushNotificationApi.initialize(applicationContext)

        setContent {
            MaterialTheme {
                AssistantApp(
                    onLaunchSpeechRecognition = {
                        startActivity(Intent(this, SpeechRecognitionActivity::class.java))
                    },
                    onLaunchImageAnalysis = {
                        startActivity(Intent(this, ImageAnalysisActivity::class.java))
                    },
                    onOpenBiometricSettings = {
                        BiometricAuth.startFingerprintManager(this)
                    }
                )
            }
        }
    }
}
