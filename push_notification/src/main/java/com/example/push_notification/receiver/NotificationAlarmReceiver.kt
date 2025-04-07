package com.example.push_notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.push_notification.PushNotificationManager
import com.example.push_notification.data.model.NotificationData
import com.example.push_notification.data.repository.NotificationRepository
import com.example.push_notification.util.Constants
import org.koin.java.KoinJavaComponent.inject

/**
 * 通知闹钟接收器
 * 用于接收定时闹钟广播并显示推送通知
 */
class NotificationAlarmReceiver : BroadcastReceiver() {

    private val notificationRepository: NotificationRepository by inject(NotificationRepository::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("NotificationAlarmReceiver", "接收到通知闹钟广播")

        // 从Intent中提取通知数据
        val title = intent.getStringExtra(Constants.EXTRA_NOTIFICATION_TITLE) ?: "推送通知"
        val message = intent.getStringExtra(Constants.EXTRA_NOTIFICATION_MESSAGE) ?: "您有一条新消息"
        val notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID, System.currentTimeMillis().toInt())

        // 保存通知到数据库
        val notificationData = NotificationData(
            id = notificationId.toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        notificationRepository.addNotification(notificationData)
        android.util.Log.d("NotificationAlarmReceiver", "通知已保存: $notificationData")

        // 显示通知
        PushNotificationManager.showNotification(
            context = context,
            title = title,
            message = message,
            notificationId = notificationId
        )
        android.util.Log.d("NotificationAlarmReceiver", "通知已显示，ID: $notificationId")
    }
} 