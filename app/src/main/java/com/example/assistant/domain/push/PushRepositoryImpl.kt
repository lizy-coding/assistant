package com.example.assistant.domain.push

import android.content.Context
import com.example.push_notification.api.PushNotificationApi
import kotlinx.coroutines.flow.StateFlow

class PushRepositoryImpl(context: Context) : PushRepository {
    private val appContext = context.applicationContext

    init {
        PushNotificationApi.initialize(appContext)
    }

    override val notificationCount: StateFlow<Int> =
        PushNotificationApi.observeNotificationCount(appContext)

    override fun refreshCount(): Int = PushNotificationApi.getNotificationCount(appContext)

    override fun release() {
        // 暂无需要释放的资源，但保留接口以便未来扩展
    }
}
