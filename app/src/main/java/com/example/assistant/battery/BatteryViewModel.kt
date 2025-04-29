package com.example.assistant.battery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery_monitor.api.BatteryMonitorApi
import com.example.battery_monitor.model.BatteryInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 电池信息ViewModel
 * 负责处理电池数据并提供给UI层
 */
class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    // 电池信息状态
    private val _batteryState = MutableStateFlow<BatteryInfo?>(null)
    val batteryState: StateFlow<BatteryInfo?> = _batteryState.asStateFlow()

    // 历史数据
    private val _batteryHistory = MutableStateFlow<List<BatteryInfo>>(emptyList())
    val batteryHistory: StateFlow<List<BatteryInfo>> = _batteryHistory.asStateFlow()

    init {
        // 初始化电池监视器
        BatteryMonitorApi.initialize(application.applicationContext)
        
        // 获取当前电池信息
        _batteryState.value = BatteryMonitorApi.getCurrentBatteryInfo()
        
        // 观察电池变化
        viewModelScope.launch {
            BatteryMonitorApi.observeBatteryChanges().collect { batteryInfo ->
                _batteryState.value = batteryInfo
                loadBatteryHistory()
            }
        }
        
        // 加载历史记录
        loadBatteryHistory()
    }

    /**
     * 加载电池历史记录
     */
    private fun loadBatteryHistory() {
        _batteryHistory.value = BatteryMonitorApi.getBatteryHistory()
    }

    /**
     * 手动刷新电池状态
     */
    fun refreshBatteryInfo() {
        BatteryMonitorApi.requestBatteryUpdate(getApplication())
    }

    /**
     * 清除历史记录
     */
    fun clearHistory() {
        BatteryMonitorApi.clearBatteryHistory()
        _batteryHistory.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        // 释放电池监视器资源
        BatteryMonitorApi.release(getApplication())
    }
} 