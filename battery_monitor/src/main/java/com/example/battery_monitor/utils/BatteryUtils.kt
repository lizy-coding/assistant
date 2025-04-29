package com.example.battery_monitor.utils

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.example.battery_monitor.model.BatteryHealth
import com.example.battery_monitor.model.BatteryInfo
import com.example.battery_monitor.model.ChargingSource

/**
 * 电池工具类，用于解析电池相关信息
 */
object BatteryUtils {

    /**
     * 从Intent中解析BatteryInfo对象
     * 
     * @param intent ACTION_BATTERY_CHANGED广播intent
     * @return 电池信息对象
     */
    fun extractBatteryInfo(intent: Intent): BatteryInfo {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryLevel = if (level != -1 && scale != -1) {
            (level * 100 / scale.toFloat()).toInt()
        } else {
            0
        }

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val chargingSource = ChargingSource.fromValue(chargePlug)

        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f // 单位转换为摄氏度
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000f // 单位转换为伏特

        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val batteryHealth = BatteryHealth.fromValue(health)

        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

        return BatteryInfo(
            level = batteryLevel,
            isCharging = isCharging,
            temperature = temperature,
            voltage = voltage,
            health = batteryHealth,
            technology = technology,
            chargingSource = chargingSource
        )
    }

    /**
     * 获取电池健康状态描述
     *
     * @param health 健康状态枚举
     * @return 健康状态描述
     */
    fun getHealthDescription(health: BatteryHealth): String {
        return health.description
    }

    /**
     * 获取充电来源描述
     *
     * @param source 充电来源枚举
     * @return 充电来源描述
     */
    fun getChargingSourceDescription(source: ChargingSource): String {
        return source.description
    }

    /**
     * 创建电池状态IntentFilter
     * 
     * @return 用于接收电池状态变化的IntentFilter
     */
    fun createBatteryIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
    }
} 