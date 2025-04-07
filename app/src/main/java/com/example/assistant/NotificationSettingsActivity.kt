package com.example.assistant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 推送通知设置界面
 * 仅作为一个中转页面，将请求转发给push_notification模块中的真正实现
 */
class NotificationSettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // TODO: 未来可能需要启动推送通知的设置界面，暂时留空
        android.util.Log.d("NotificationSettings", "启动推送通知设置界面（占位）")
        
        // 关闭当前活动，不需要显示
        finish()
    }
}