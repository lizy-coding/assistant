package com.example.push_notification.service

import android.annotation.SuppressLint
import com.example.push_notification.data.model.NotificationData
import com.example.push_notification.data.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val notificationRepository: NotificationRepository by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let { notification ->
            val notificationData = NotificationData(
                id = remoteMessage.messageId ?: "",
                title = notification.title ?: "",
                message = notification.body ?: "",
                timestamp = System.currentTimeMillis()
            )
            notificationRepository.addNotification(notificationData)
        }
    }
}