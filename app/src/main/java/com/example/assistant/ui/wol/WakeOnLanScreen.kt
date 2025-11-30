package com.example.assistant.ui.wol

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wake_on_lan.permission.WakeOnLanPermissions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeOnLanScreen(viewModel: WakeOnLanViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionToRequest = WakeOnLanPermissions.nearbyWifiPermission
    val permissionGranted = remember(permissionToRequest, state.permissionGranted) {
        permissionToRequest == null || ContextCompat.checkSelfPermission(
            context,
            permissionToRequest
        ) == PackageManager.PERMISSION_GRANTED || state.permissionGranted
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updatePermissionGranted(granted || permissionToRequest == null)
    }

    LaunchedEffect(permissionGranted) {
        viewModel.updatePermissionGranted(permissionGranted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("局域网唤醒") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "填写目标信息并发送 Magic Packet 唤醒局域网设备。",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = state.macAddress,
                onValueChange = viewModel::updateMacAddress,
                label = { Text("目标 MAC 地址") },
                placeholder = { Text("AA:BB:CC:DD:EE:FF") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.broadcastAddress,
                onValueChange = viewModel::updateBroadcastAddress,
                label = { Text("广播地址") },
                placeholder = { Text("255.255.255.255") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.port,
                onValueChange = viewModel::updatePort,
                label = { Text("端口") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (permissionToRequest != null && !permissionGranted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "发送唤醒包需要授予局域网相关权限。",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { permissionLauncher.launch(permissionToRequest) }) {
                        Text("申请权限")
                    }
                }
            }

            Button(
                onClick = {
                    if (permissionToRequest != null &&
                        ContextCompat.checkSelfPermission(
                            context,
                            permissionToRequest
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(permissionToRequest)
                    } else {
                        viewModel.sendMagicPacket()
                    }
                },
                enabled = !state.isSending && state.macAddress.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("发送中...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("发送唤醒包")
                }
            }

            state.statusMessage?.let { message ->
                val isError = message.startsWith("发送失败")
                Text(
                    text = message,
                    color = if (isError) MaterialTheme.colorScheme.error else Color(0xFF0F9D58),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
