# Assistant 应用

Android 助手应用，聚合语音识别、图像分析、生物识别、推送通知、电池监控等能力，首页展示综合仪表盘（电池状态 + 推送消息数），支持 Compose 与传统 ViewBinding 共存。


## 项目结构
```text
Assistant/
├── app/                   # 主应用模块
├── biometric_auth/        # 生物识别认证模块
├── speech_recognition/    # 语音识别模块
├── image_analysis/        # 图像分析模块
├── push_notification/     # 推送通知模块
└── battery_monitor/       # 电池监控模块
```


## 项目鱼骨图（模块视图）
```
                               [app]
                                 |
             -------------------------------------------------
             |           |            |            |         |
     [domain+ui]   [push_notification] [battery_monitor] [speech_recognition]
             |           |            |            |         |
     (main/battery/   推送调度、    电池状态      百度语音   ┐
      notification/   仓库统计、    Flow+历史     示例与    |
      speech/image/   Koin 单例)    API 出口      SDK      |
      biometric)                                        [image_analysis]
                                                        图像采集/OCR
                                                        |
                                               [biometric_auth]
                                               指纹/面部认证
```

## 模块职责
- `app/`：主应用壳。`domain/` 存放仓库与用例（BatteryRepository/PushRepository 等）；`ui/` 按功能拆包（`main` 首页导航与仪表盘，`battery` 电池监控，`notification` 推送设置，`speech` 语音入口，`image` 图像入口，`biometric` 中转）。
- `push_notification/`：推送中心；初始化 Koin，管理通知渠道、定时 Alarm、消息仓库并提供计数 StateFlow。
- `battery_monitor/`：电池广播监听与解析，提供 `BatteryMonitorApi`、实时 Flow 与历史列表。
- `speech_recognition/`：百度语音识别示例与基础封装。
- `image_analysis/`：摄像头采集、OCR/活体流程的演示模块。
- `biometric_auth/`：指纹/面部认证与相关 UI。

## 整体运行逻辑
1) `ui/main/MainActivity` 启动，初始化推送模块，加载 Compose `AssistantApp` 导航。  
2) `HomeViewModel` 通过 `ObserveDashboardUseCase` 组合 `BatteryRepository` 与 `PushRepository` 的 StateFlow，驱动首页仪表盘。  
3) 电池数据：`BatteryMonitorApi` 监听系统广播，写入历史并推送 Flow；`BatteryRepository` 缓存并提供刷新/清理。  
4) 推送数据：`PushNotificationApi` 初始化 Koin 单例仓库，调度/即时推送都会写入仓库；`PushRepository` 观察消息数量流，用于仪表盘统计。  
5) 其他功能入口（语音、图像、生物识别）从首页卡片跳转对应 Activity/Fragment。

## 主要入口文件
- 首页 Activity：`app/src/main/java/com/example/assistant/ui/main/MainActivity.kt`
- 首页 Compose：`app/src/main/java/com/example/assistant/ui/main/AssistantApp.kt`
- 推送设置：`app/src/main/java/com/example/assistant/ui/notification/NotificationSettingsActivity.kt`
- 电池监控：`app/src/main/java/com/example/assistant/ui/battery/BatteryActivity.kt`

## 开发环境
- JDK 17
- Android Studio Koala / Iguana 及以上
- compileSdk 35，minSdk 30


## 常用命令
- 构建 Debug：`./gradlew :app:assembleDebug`
- JVM 测试：`./gradlew test`
- 安装调试包：`./gradlew :app:installDebug`
- Lint：`./gradlew lint`

> 当前仓库的 `gradlew` 仍是 Windows 行尾，类 UNIX 环境若报 `sh\r` 请先 `dos2unix gradlew` 或重新生成 wrapper。 

