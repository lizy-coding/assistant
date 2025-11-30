package com.example.assistant.domain.battery

import android.content.Context
import com.example.battery_monitor.api.BatteryMonitorApi
import com.example.battery_monitor.model.BatteryInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatteryRepositoryImpl(context: Context) : BatteryRepository {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _batteryState = MutableStateFlow<BatteryInfo?>(null)
    private val _batteryHistory = MutableStateFlow<List<BatteryInfo>>(emptyList())

    init {
        _batteryState.value = BatteryMonitorApi.initialize(appContext)
        _batteryHistory.value = BatteryMonitorApi.getBatteryHistory()
        scope.launch {
            BatteryMonitorApi.observeBatteryChanges().collect { info ->
                _batteryState.value = info
                _batteryHistory.value = BatteryMonitorApi.getBatteryHistory()
            }
        }
    }

    override val batteryState: StateFlow<BatteryInfo?> = _batteryState.asStateFlow()
    override val batteryHistory: StateFlow<List<BatteryInfo>> = _batteryHistory.asStateFlow()

    override fun refresh() {
        BatteryMonitorApi.requestBatteryUpdate(appContext)
    }

    override fun clearHistory() {
        BatteryMonitorApi.clearBatteryHistory()
        _batteryHistory.value = emptyList()
    }

    override fun release() {
        BatteryMonitorApi.release(appContext)
        scope.cancel()
    }
}
