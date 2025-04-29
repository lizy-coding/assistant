package com.example.battery_monitor.model

/**
 * 电池信息数据类
 *
 * @property level 电池电量百分比 (0-100)
 * @property isCharging 电池是否正在充电
 * @property temperature 电池温度，单位为摄氏度
 * @property voltage 电池电压，单位为伏特
 * @property health 电池健康状态
 * @property technology 电池技术类型
 * @property chargingSource 充电来源
 * @property timestamp 记录时间戳
 */
data class BatteryInfo(
    val level: Int,
    val isCharging: Boolean,
    val temperature: Float,
    val voltage: Float,
    val health: BatteryHealth,
    val technology: String,
    val chargingSource: ChargingSource,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 电池健康状态枚举
 */
enum class BatteryHealth(val value: Int, val description: String) {
    UNKNOWN(1, "未知"),
    GOOD(2, "良好"),
    OVERHEAT(3, "过热"),
    DEAD(4, "损坏"),
    OVER_VOLTAGE(5, "过压"),
    UNSPECIFIED_FAILURE(6, "未明确故障"),
    COLD(7, "过冷");

    companion object {
        fun fromValue(value: Int): BatteryHealth {
            return values().find { it.value == value } ?: UNKNOWN
        }
    }
}

/**
 * 充电来源枚举
 */
enum class ChargingSource(val value: Int, val description: String) {
    NONE(0, "未充电"),
    AC(1, "交流电源"),
    USB(2, "USB"),
    WIRELESS(4, "无线充电"),
    DOCK(8, "底座充电"),
    UNKNOWN(-1, "未知来源");

    companion object {
        fun fromValue(value: Int): ChargingSource {
            return values().find { it.value == value } ?: UNKNOWN
        }
    }
} 