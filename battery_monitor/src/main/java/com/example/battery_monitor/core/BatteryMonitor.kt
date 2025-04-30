package com.example.battery_monitor.core

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.example.battery_monitor.model.BatteryInfo
import com.example.battery_monitor.utils.BatteryUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 电池监视器核心类
 * 提供注册/注销电池广播接收器、获取电池信息的功能
 */
object BatteryMonitor {
    private var receiver: BatteryReceiver? = null
    private var isRegistered = false

    // 电池信息观察事件流
    private val _batteryEvents = MutableSharedFlow<BatteryInfo>(replay = 1)
    val batteryEvents: Flow<BatteryInfo> = _batteryEvents.asSharedFlow()

    // 电池信息记录列表，用于保存历史数据
    private val batteryInfoHistory = mutableListOf<BatteryInfo>()
    // 最大历史记录数
    private const val MAX_HISTORY_SIZE = 100

    /**
     * 初始化并注册电池状态监听器
     *
     * @param context 应用上下文
     * @return 当前电池状态信息
     */
    fun initialize(context: Context): BatteryInfo? {
        if (isRegistered) return BatteryReceiver.getCurrentBatteryInfo()

        // 创建并注册广播接收器
        receiver = BatteryReceiver()
        val intentFilter = BatteryUtils.createBatteryIntentFilter()
        ContextCompat.registerReceiver(
            context.applicationContext,
            receiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        isRegistered = true

        // 请求一次电池状态更新
        return BatteryReceiver.requestBatteryUpdate(context)
    }

    /**
     * 注销电池状态监听器
     *
     * @param context 应用上下文
     */
    fun release(context: Context) {
        if (!isRegistered || receiver == null) return

        try {
            context.applicationContext.unregisterReceiver(receiver)
        } catch (e: Exception) {
            // 处理可能的异常
        } finally {
            receiver = null
            isRegistered = false
        }
    }

    /**
     * 手动请求电池状态更新
     *
     * @param context 应用上下文
     * @return 最新的电池信息或null（如果无法获取）
     */
    fun requestUpdate(context: Context): BatteryInfo? {
        return BatteryReceiver.requestBatteryUpdate(context)
    }

    /**
     * 获取当前电池信息
     *
     * @return 电池信息对象或null（如果未初始化）
     */
    fun getCurrentBatteryInfo(): BatteryInfo? {
        return BatteryReceiver.getCurrentBatteryInfo()
    }

    /**
     * 获取电池信息历史记录
     *
     * @return 电池信息历史记录列表
     */
    fun getBatteryInfoHistory(): List<BatteryInfo> {
        return batteryInfoHistory.toList()
    }

    /**
     * 清除电池信息历史记录
     */
    fun clearBatteryInfoHistory() {
        batteryInfoHistory.clear()
    }

    /**
     * 处理电池信息更新
     * 由BatteryReceiver调用
     *
     * @param info 更新的电池信息
     */
    internal fun onBatteryInfoUpdated(info: BatteryInfo) {
        // 更新历史记录
        batteryInfoHistory.add(info)
        if (batteryInfoHistory.size > MAX_HISTORY_SIZE) {
            batteryInfoHistory.removeAt(0)
        }

        // 发送更新事件
        _batteryEvents.tryEmit(info)
    }
} 