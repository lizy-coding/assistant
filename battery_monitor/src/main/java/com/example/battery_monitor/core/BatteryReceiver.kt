package com.example.battery_monitor.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.example.battery_monitor.utils.BatteryUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 电池状态广播接收器
 * 负责接收系统电池状态变化广播并更新电池信息
 */
class BatteryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val action = intent.action
        when (action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                val batteryInfo = BatteryUtils.extractBatteryInfo(intent)
                _batteryInfo.value = batteryInfo
                BatteryMonitor.onBatteryInfoUpdated(batteryInfo)
            }
            Intent.ACTION_POWER_CONNECTED -> {
                // 电源已连接
                // 可以在这里执行特定的电源连接逻辑
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                // 电源已断开
                // 可以在这里执行特定的电源断开逻辑
            }
        }
    }

    companion object {
        // 电池信息StateFlow，可被观察以获取最新电池状态
        private val _batteryInfo = MutableStateFlow<com.example.battery_monitor.model.BatteryInfo?>(null)
        val batteryInfo = _batteryInfo.asStateFlow()

        /**
         * 获取当前电池信息（如果已注册接收器）
         */
        fun getCurrentBatteryInfo(): com.example.battery_monitor.model.BatteryInfo? {
            return _batteryInfo.value
        }

        /**
         * 手动触发电池信息更新
         *
         * @param context 上下文
         * @return 最新的电池信息或null（如果无法获取）
         */
        fun requestBatteryUpdate(context: Context): com.example.battery_monitor.model.BatteryInfo? {
            val intent = context.registerReceiver(null, BatteryUtils.createBatteryIntentFilter())
            return if (intent != null) {
                val batteryInfo = BatteryUtils.extractBatteryInfo(intent)
                _batteryInfo.value = batteryInfo
                BatteryMonitor.onBatteryInfoUpdated(batteryInfo)
                batteryInfo
            } else {
                null
            }
        }
    }
} 