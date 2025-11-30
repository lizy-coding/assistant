package com.example.assistant.ui.battery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assistant.domain.battery.BatteryRepository
import com.example.assistant.domain.battery.BatteryRepositoryProvider
import com.example.battery_monitor.model.BatteryInfo
import kotlinx.coroutines.flow.StateFlow

/**
 * 电池信息ViewModel
 * 负责处理电池数据并提供给UI层
 */
class BatteryViewModel(
    application: Application,
    private val batteryRepository: BatteryRepository = BatteryRepositoryProvider.acquire(application)
) : AndroidViewModel(application) {

    val batteryState: StateFlow<BatteryInfo?> = batteryRepository.batteryState
    val batteryHistory: StateFlow<List<BatteryInfo>> = batteryRepository.batteryHistory

    /**
     * 手动刷新电池状态
     */
    fun refreshBatteryInfo() {
        batteryRepository.refresh()
    }

    /**
     * 清除历史记录
     */
    fun clearHistory() {
        batteryRepository.clearHistory()
    }

    override fun onCleared() {
        super.onCleared()
        BatteryRepositoryProvider.release()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as Application)
                BatteryViewModel(application)
            }
        }
    }
} 
