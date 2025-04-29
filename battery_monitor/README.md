# 电池监视器模块 (Battery Monitor)

电池监视器模块用于监控设备电池状态，并提供电池信息的实时更新和历史记录功能。

## 功能特性

- 实时监控电池状态变化
- 提供电池电量、温度、电压等详细信息
- 支持充电状态监测
- 电池健康状态评估
- 电池历史数据记录与分析

## 集成方式

在项目的`settings.gradle`文件中添加模块依赖：

```gradle
include ':battery_monitor'
```

在应用模块的`build.gradle`文件中添加依赖：

```gradle
implementation project(':battery_monitor')
```

## 使用方法

### 初始化

在应用启动时（如在Application或主Activity的onCreate中）初始化电池监视器：

```kotlin
// 在Application或Activity中初始化
BatteryMonitorApi.initialize(applicationContext)
```

### 获取当前电池信息

```kotlin
val batteryInfo = BatteryMonitorApi.getCurrentBatteryInfo()
batteryInfo?.let {
    // 使用电池信息
    val level = it.level // 电量百分比
    val isCharging = it.isCharging // 是否充电中
    val temperature = it.temperature // 温度（摄氏度）
    val voltage = it.voltage // 电压（伏特）
}
```

### 观察电池状态变化

使用Kotlin Flow观察电池状态实时变化：

```kotlin
// 在ViewModel中
private val _batteryState = MutableStateFlow<BatteryInfo?>(null)
val batteryState: StateFlow<BatteryInfo?> = _batteryState

init {
    viewModelScope.launch {
        BatteryMonitorApi.observeBatteryChanges().collect {
            _batteryState.value = it
        }
    }
}
```

或者在UI中直接收集：

```kotlin
// 在Activity/Fragment中
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        BatteryMonitorApi.observeBatteryChanges().collect { batteryInfo ->
            // 更新UI
            updateBatteryUI(batteryInfo)
        }
    }
}
```

### 获取电池历史数据

```kotlin
val batteryHistory = BatteryMonitorApi.getBatteryHistory()
// 处理历史数据，如显示图表等
```

### 释放资源

在应用退出时释放资源：

```kotlin
override fun onDestroy() {
    super.onDestroy()
    BatteryMonitorApi.release(applicationContext)
}
```

## 权限要求

模块自动申请以下权限：

```xml
<uses-permission android:name="android.permission.BATTERY_STATS" />
```

## API文档

### BatteryMonitorApi

主要API接口类，包含以下方法：

- `initialize(context: Context)`: 初始化电池监视器
- `release(context: Context)`: 释放资源
- `observeBatteryChanges()`: 获取电池状态变化Flow
- `getCurrentBatteryInfo()`: 获取当前电池信息
- `requestBatteryUpdate(context: Context)`: 手动请求电池状态更新
- `getBatteryHistory()`: 获取电池历史记录
- `clearBatteryHistory()`: 清除电池历史记录

### BatteryInfo

电池信息数据类，包含以下属性：

- `level`: 电池电量百分比 (0-100)
- `isCharging`: 是否正在充电
- `temperature`: 电池温度（摄氏度）
- `voltage`: 电池电压（伏特）
- `health`: 电池健康状态（BatteryHealth枚举）
- `technology`: 电池技术类型
- `chargingSource`: 充电来源（ChargingSource枚举）
- `timestamp`: 记录时间戳 