package com.example.assistant.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assistant.domain.push.ScheduleNotificationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

/**
 * 推送通知设置的UI状态
 */
data class NotificationSettingsUiState(
    val title: String = "",
    val message: String = "",
    val delayMinutes: Float = 10f
)

class NotificationSettingsViewModel(
    application: Application,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase = ScheduleNotificationUseCase(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun updateDelayMinutes(delay: Float) {
        _uiState.update { it.copy(delayMinutes = delay.coerceIn(1f, 60f)) }
    }

    /**
     * 调度推送通知，返回是否调度成功（标题与内容不能为空）
     */
    fun scheduleNotification(): Boolean {
        val state = _uiState.value
        val success = scheduleNotificationUseCase(
            title = state.title,
            message = state.message,
            delayMinutes = state.delayMinutes.roundToInt()
        )
        if (success) {
            _uiState.update { it.copy(title = "", message = "") }
        }
        return success
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as Application)
                NotificationSettingsViewModel(application)
            }
        }
    }
}
