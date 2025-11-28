package com.example.assistant.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.example.assistant.domain.battery.BatteryRepository
import com.example.assistant.domain.battery.BatteryRepositoryProvider
import com.example.assistant.domain.dashboard.DashboardState
import com.example.assistant.domain.dashboard.ObserveDashboardUseCase
import com.example.assistant.domain.push.PushRepository
import com.example.assistant.domain.push.PushRepositoryProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    application: Application,
    private val batteryRepository: BatteryRepository = BatteryRepositoryProvider.acquire(application),
    private val pushRepository: PushRepository = PushRepositoryProvider.acquire(application),
    observeDashboardUseCase: ObserveDashboardUseCase = ObserveDashboardUseCase(
        batteryRepository,
        pushRepository
    )
) : AndroidViewModel(application) {

    private val initialState: DashboardState
        get() = DashboardState(
            batteryInfo = batteryRepository.batteryState.value,
            pushMessageCount = pushRepository.notificationCount.value
        )

    val dashboardState: StateFlow<DashboardState> = observeDashboardUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initialState
        )

    fun refreshBattery() {
        batteryRepository.refresh()
    }

    fun clearBatteryHistory() {
        batteryRepository.clearHistory()
    }

    override fun onCleared() {
        super.onCleared()
        BatteryRepositoryProvider.release()
        PushRepositoryProvider.release()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                HomeViewModel(app)
            }
        }
    }
}
