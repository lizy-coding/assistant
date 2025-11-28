package com.example.assistant.domain.push

import android.content.Context
import com.example.push_notification.api.PushNotificationApi

class ScheduleNotificationUseCase(context: Context) {
    private val appContext = context.applicationContext

    operator fun invoke(title: String, message: String, delayMinutes: Int): Boolean {
        if (title.isBlank() || message.isBlank()) return false

        PushNotificationApi.scheduleNotification(
            context = appContext,
            title = title,
            message = message,
            delayMinutes = delayMinutes
        )
        return true
    }
}
