package com.example.assistant.domain.dashboard

import com.example.battery_monitor.model.BatteryInfo

data class DashboardState(
    val batteryInfo: BatteryInfo? = null,
    val pushMessageCount: Int = 0
)
