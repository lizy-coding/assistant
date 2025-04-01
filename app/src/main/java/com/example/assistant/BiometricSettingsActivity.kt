package com.example.assistant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.biometric_auth.api.BiometricAuth

/**
 * 生物识别设置界面
 * 仅作为一个中转页面，将请求转发给biometric_auth模块中的真正实现
 */
class BiometricSettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启动生物识别模块中的指纹管理活动
        BiometricAuth.startFingerprintManager(this)
        
        // 关闭当前活动，不需要显示
        finish()
    }
}