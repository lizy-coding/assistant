package com.example.push_notification.api

import android.content.Context
import android.content.Intent
import com.example.push_notification.PushNotificationInitializer
import com.example.push_notification.PushNotificationManager
import com.example.push_notification.data.model.NotificationData
import com.example.push_notification.data.repository.NotificationRepository
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.context.GlobalContext

/**
 * 推送通知模块对外API
 * 提供类似image_analysis和biometric_auth模块的入口点
 */
object PushNotificationApi {

    /**
     * 初始化推送通知模块
     * 在应用启动时调用
     *
     * @param context 应用上下文
     */
    fun initialize(context: Context) {
        PushNotificationInitializer.initialize(context.applicationContext)
    }

    /**
     * 立即发送一条推送通知
     *
     * @param context 上下文
     * @param title 通知标题
     * @param message 通知内容
     */
    fun sendNotification(context: Context, title: String, message: String) {
        val appContext = context.applicationContext
        // 确保模块已初始化
        initialize(appContext)

        val notificationId = System.currentTimeMillis().toInt()
        val notification = NotificationData(
            id = notificationId.toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        // 记录推送中心数据
        getRepository().addNotification(notification)

        // 直接显示通知
        PushNotificationManager.showNotification(
            context = appContext,
            title = title,
            message = message,
            notificationId = notificationId
        )
    }

    /**
     * 设置延迟推送通知
     *
     * @param context 上下文
     * @param title 通知标题
     * @param message 通知内容
     * @param delayMinutes 延迟分钟数
     */
    fun scheduleNotification(
        context: Context,
        title: String,
        message: String,
        delayMinutes: Int = -1
    ) {
        if (delayMinutes <= 0) {
            sendNotification(
                context = context,
                title = title,
                message = message
            )
            return
        }
        // 确保模块已初始化
        initialize(context)
        
        // 调度延迟通知
        PushNotificationManager.scheduleNotification(
            context = context,
            title = title,
            message = message,
            delayMinutes = delayMinutes
        )
    }

    /**
     * 启动通知设置界面
     *
     * @param context 启动上下文
     */
    fun startNotificationSettingsActivity(context: Context) {
        // 确保模块已初始化
        initialize(context)
        
        // 通过Intent启动Activity
        try {
            val intent = Intent(context, Class.forName("com.example.assistant.ui.notification.NotificationSettingsActivity"))
            context.startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("PushNotificationApi", "启动通知设置界面失败", e)
        }
    }

    /**
     * 观察推送中心消息数量
     */
    fun observeNotificationCount(context: Context): StateFlow<Int> {
        return getRepository(context).notificationCount
    }

    /**
     * 获取当前推送中心消息数量
     */
    fun getNotificationCount(context: Context): Int {
        return getRepository(context).getNotificationCount()
    }

    private fun getRepository(context: Context? = null): NotificationRepository {
        context?.let { initialize(it) }
        val koin = GlobalContext.getOrNull()
            ?: throw IllegalStateException("PushNotificationInitializer is not started. Call initialize(context) first.")
        return koin.get()
    }
} 
