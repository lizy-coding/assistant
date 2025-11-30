package com.example.assistant.ui.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.battery_monitor.model.BatteryInfo
import java.text.SimpleDateFormat
import java.util.*

/**
 * 电池信息屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(viewModel: BatteryViewModel = viewModel(factory = BatteryViewModel.Factory)) {
    val batteryState by viewModel.batteryState.collectAsState()
    val batteryHistory by viewModel.batteryHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("电池信息") },
                actions = {
                    IconButton(onClick = { viewModel.refreshBatteryInfo() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                    IconButton(onClick = { viewModel.clearHistory() }) {
                        Icon(Icons.Default.Delete, contentDescription = "清除历史")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 当前电池状态卡片
            batteryState?.let { info ->
                BatteryInfoCard(info)
            } ?: run {
                Text("加载电池信息中...", style = MaterialTheme.typography.bodyLarge)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 历史记录标题
            Text(
                text = "电池历史记录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 历史记录列表
            if (batteryHistory.isNotEmpty()) {
                LazyColumn {
                    items(batteryHistory) { historyItem ->
                        BatteryHistoryItem(historyItem)
                        Divider()
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无历史记录", color = Color.Gray)
                }
            }
        }
    }
}

/**
 * 电池信息卡片
 */
@Composable
fun BatteryInfoCard(info: BatteryInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 电池电量进度条
            Text(
                text = "当前电量: ${info.level}%",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = info.level / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = when {
                    info.level <= 20 -> Color.Red
                    info.level <= 50 -> Color(0xFFFFA500) // Orange
                    else -> Color.Green
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 充电状态
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "充电状态: ",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (info.isCharging) "充电中 (${info.chargingSource.description})" else "未充电",
                    color = if (info.isCharging) Color.Green else Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 其他电池信息
            Text("温度: ${info.temperature}°C")
            Text("电压: ${info.voltage}V")
            Text("健康状态: ${info.health.description}")
            Text("电池技术: ${info.technology}")
        }
    }
}

/**
 * 历史记录项
 */
@Composable
fun BatteryHistoryItem(info: BatteryInfo) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(info.timestamp))
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 电量指示器
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = when {
                        info.level <= 20 -> Color.Red
                        info.level <= 50 -> Color(0xFFFFA500) // Orange
                        else -> Color.Green
                    },
                    shape = MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${info.level}%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (info.isCharging) "充电中 (${info.chargingSource.description})" else "未充电",
                style = MaterialTheme.typography.bodySmall,
                color = if (info.isCharging) Color.Green else Color.Gray
            )
            Text(
                text = "温度: ${info.temperature}°C, 电压: ${info.voltage}V",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 
