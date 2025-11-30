package com.example.push_notification.data.repository

import com.example.push_notification.data.model.NotificationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationRepository {
    private val notifications = MutableStateFlow<List<NotificationData>>(emptyList())
    private val _notificationCount = MutableStateFlow(0)

    val notificationStream: StateFlow<List<NotificationData>> = notifications.asStateFlow()
    val notificationCount: StateFlow<Int> = _notificationCount.asStateFlow()

    fun addNotification(notification: NotificationData) {
        notifications.update { current ->
            val updated = current + notification
            _notificationCount.value = updated.size
            updated
        }
    }

    fun getNotifications(): List<NotificationData> {
        return notifications.value
    }

    fun getNotificationCount(): Int = _notificationCount.value
}
