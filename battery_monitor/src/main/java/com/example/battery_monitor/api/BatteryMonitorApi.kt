package com.example.battery_monitor.api

import android.content.Context
import com.example.battery_monitor.core.BatteryMonitor
import com.example.battery_monitor.model.BatteryInfo
import kotlinx.coroutines.flow.Flow

/**
 * 电池监视器API类
 * 提供给外部使用的公共接口，是整个模块的对外入口点
 */
object BatteryMonitorApi {

    /**
     * 初始化电池监视器
     * 应在应用启动时调用
     *
     * @param context 应用上下文
     * @return 当前电池状态信息
     */
    fun initialize(context: Context): BatteryInfo? {
        return BatteryMonitor.initialize(context)
    }

    /**
     * 释放电池监视器资源
     * 应在应用退出时调用
     *
     * @param context 应用上下文
     */
    fun release(context: Context) {
        BatteryMonitor.release(context)
    }

    /**
     * 获取电池状态变化的Flow
     * 可以通过此Flow观察电池状态的实时变化
     *
     * @return 电池信息Flow
     */
    fun observeBatteryChanges(): Flow<BatteryInfo> {
        return BatteryMonitor.batteryEvents
    }

    /**
     * 获取当前电池信息
     *
     * @return 当前电池信息或null（如果未初始化）
     */
    fun getCurrentBatteryInfo(): BatteryInfo? {
        return BatteryMonitor.getCurrentBatteryInfo()
    }

    /**
     * 手动请求更新电池信息
     *
     * @param context 上下文
     * @return 更新后的电池信息
     */
    fun requestBatteryUpdate(context: Context): BatteryInfo? {
        return BatteryMonitor.requestUpdate(context)
    }

    /**
     * 获取电池信息历史记录
     *
     * @return 电池信息历史记录列表
     */
    fun getBatteryHistory(): List<BatteryInfo> {
        return BatteryMonitor.getBatteryInfoHistory()
    }

    /**
     * 清除电池历史记录
     */
    fun clearBatteryHistory() {
        BatteryMonitor.clearBatteryInfoHistory()
    }
} 