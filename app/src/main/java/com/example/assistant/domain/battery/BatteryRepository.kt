package com.example.assistant.domain.battery

import com.example.battery_monitor.model.BatteryInfo
import kotlinx.coroutines.flow.StateFlow

interface BatteryRepository {
    val batteryState: StateFlow<BatteryInfo?>
    val batteryHistory: StateFlow<List<BatteryInfo>>

    fun refresh()
    fun clearHistory()
    fun release()
}
