package com.example.assistant.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assistant.domain.dashboard.DashboardState
import com.example.assistant.ui.battery.BatteryScreen
import com.example.assistant.ui.home.HomeViewModel
import com.example.assistant.ui.notification.NotificationSettingsScreen

/**
 * 应用的导航目的地
 */
private enum class AssistantDestination(val route: String) {
    Home("home"),
    NotificationSettings("notification_settings"),
    Battery("battery")
}

@Composable
fun AssistantApp(
    onLaunchSpeechRecognition: () -> Unit,
    onLaunchImageAnalysis: () -> Unit,
    onOpenBiometricSettings: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AssistantDestination.Home.route
    ) {
        composable(AssistantDestination.Home.route) {
            HomeScreen(
                onLaunchSpeechRecognition = onLaunchSpeechRecognition,
                onLaunchImageAnalysis = onLaunchImageAnalysis,
                onOpenBiometricSettings = onOpenBiometricSettings,
                onOpenNotificationSettings = {
                    navController.navigate(AssistantDestination.NotificationSettings.route)
                },
                onOpenBatteryMonitor = {
                    navController.navigate(AssistantDestination.Battery.route)
                }
            )
        }

        composable(AssistantDestination.NotificationSettings.route) {
            NotificationSettingsScreen(
                onNavigateUp = { navController.popBackStack() }
            )
        }

        composable(AssistantDestination.Battery.route) {
            BatteryScreen()
        }
    }
}

private data class AssistantFeature(
    val title: String,
    val description: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreen(
    onLaunchSpeechRecognition: () -> Unit,
    onLaunchImageAnalysis: () -> Unit,
    onOpenBiometricSettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatteryMonitor: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val dashboardState by viewModel.dashboardState.collectAsState()

    val features = listOf(
        AssistantFeature(
            title = "语音识别",
            description = "体验语音识别和ASR能力",
            icon = { Icon(Icons.Default.Mic, contentDescription = null) },
            onClick = onLaunchSpeechRecognition
        ),
        AssistantFeature(
            title = "图像分析",
            description = "进行图像采集与生物识别校验",
            icon = { Icon(Icons.Default.Image, contentDescription = null) },
            onClick = onLaunchImageAnalysis
        ),
        AssistantFeature(
            title = "生物识别设置",
            description = "管理指纹等生物识别安全设置",
            icon = { Icon(Icons.Default.Biotech, contentDescription = null) },
            onClick = onOpenBiometricSettings
        ),
        AssistantFeature(
            title = "推送通知设置",
            description = "使用Jetpack Compose配置定时推送",
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
            onClick = onOpenNotificationSettings
        ),
        AssistantFeature(
            title = "电池监控",
            description = "通过Compose界面查看电池状态与历史",
            icon = { Icon(Icons.Default.BatteryFull, contentDescription = null) },
            onClick = onOpenBatteryMonitor
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "智能辅助工具") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DashboardCard(
                    state = dashboardState,
                    onRefreshBattery = viewModel::refreshBattery,
                    onOpenNotificationSettings = onOpenNotificationSettings,
                    onOpenBatteryMonitor = onOpenBatteryMonitor
                )
            }

            items(features) { feature ->
                FeatureCard(feature)
            }
        }
    }
}

@Composable
private fun DashboardCard(
    state: DashboardState,
    onRefreshBattery: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatteryMonitor: () -> Unit
) {
    val batteryInfo = state.batteryInfo
    val batteryLevel = (batteryInfo?.level ?: 0).coerceIn(0, 100)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "综合仪表盘",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BatteryFull, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "电池", style = MaterialTheme.typography.labelLarge)
                    }
                    Text(
                        text = if (batteryInfo != null) "${batteryLevel}%" else "--",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = batteryLevel / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = batteryInfo?.let {
                            if (it.isCharging) "充电中 · ${it.chargingSource.description}" else "未充电"
                        } ?: "正在读取电池状态",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    batteryInfo?.let {
                        Text(
                            text = "健康: ${it.health.description} · 温度: ${it.temperature}°C · 电压: ${it.voltage}V",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Divider(
                    modifier = Modifier
                        .height(96.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "推送中心", style = MaterialTheme.typography.labelLarge)
                    }
                    Text(
                        text = "${state.pushMessageCount}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "累积消息总数",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(onClick = onOpenNotificationSettings) {
                        Text(text = "打开推送设置")
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRefreshBattery,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "刷新电池")
                }
                Button(
                    onClick = onOpenBatteryMonitor,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "查看电池监控")
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(feature: AssistantFeature) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RowWithIcon(title = feature.title, icon = feature.icon)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = feature.onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "打开")
            }
        }
    }
}

@Composable
private fun RowWithIcon(title: String, icon: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
