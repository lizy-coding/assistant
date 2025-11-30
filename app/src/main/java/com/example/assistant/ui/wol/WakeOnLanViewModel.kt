package com.example.assistant.ui.wol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wake_on_lan.api.WakeOnLanApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WakeOnLanUiState(
    val macAddress: String = "",
    val broadcastAddress: String = "255.255.255.255",
    val port: String = "9",
    val isSending: Boolean = false,
    val statusMessage: String? = null,
    val permissionGranted: Boolean = false
)

/**
 * 负责处理局域网唤醒逻辑的 ViewModel。
 */
class WakeOnLanViewModel(
    private val wakeOnLanApi: WakeOnLanApi = WakeOnLanApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(WakeOnLanUiState())
    val uiState: StateFlow<WakeOnLanUiState> = _uiState.asStateFlow()

    fun updateMacAddress(value: String) {
        _uiState.update { it.copy(macAddress = value) }
    }

    fun updateBroadcastAddress(value: String) {
        _uiState.update { it.copy(broadcastAddress = value) }
    }

    fun updatePort(value: String) {
        _uiState.update { it.copy(port = value.filter { char -> char.isDigit() }) }
    }

    fun updatePermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(permissionGranted = granted) }
    }

    fun sendMagicPacket() {
        val current = _uiState.value
        val mac = current.macAddress.trim()
        val broadcast = current.broadcastAddress.trim().ifBlank { "255.255.255.255" }
        val portValue = current.port.toIntOrNull()

        if (!wakeOnLanApi.isValidMac(mac)) {
            _uiState.update { it.copy(statusMessage = "MAC 地址格式不正确") }
            return
        }

        if (portValue == null || portValue !in 1..65535) {
            _uiState.update { it.copy(statusMessage = "端口号无效") }
            return
        }

        _uiState.update {
            it.copy(
                isSending = true,
                statusMessage = "正在发送 Magic Packet..."
            )
        }

        viewModelScope.launch {
            val result = wakeOnLanApi.sendMagicPacket(mac, broadcast, portValue)
            _uiState.update {
                it.copy(
                    isSending = false,
                    statusMessage = result.fold(
                        onSuccess = { "唤醒包已发送" },
                        onFailure = { error -> "发送失败：${error.message ?: "未知错误"}" }
                    )
                )
            }
        }
    }
}
