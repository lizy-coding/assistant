package com.example.assistant.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assistant.battery.BatteryScreen
import com.example.assistant.notification.NotificationSettingsScreen

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
private fun HomeScreen(
    onLaunchSpeechRecognition: () -> Unit,
    onLaunchImageAnalysis: () -> Unit,
    onOpenBiometricSettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatteryMonitor: () -> Unit
) {
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
            items(features) { feature ->
                FeatureCard(feature)
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
