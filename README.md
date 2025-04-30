# Assistant 项目

## 项目概述
本项目是一个Android助手应用，包含多个功能模块，如生物识别认证、语音识别等。

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

## 模块介绍
- **biometric_auth**: 提供生物识别认证功能，支持指纹和面部识别。
- **speech_recognition**: 实现语音识别功能，支持实时语音转文字。
- **image_analysis**: 提供图像分析功能，支持图像识别和处理。
- **push_notification**: 提供消息推送和定时通知功能。
- **battery_monitor**: 监控设备电池状态，提供电池电量、温度、电压等实时数据和历史记录。

## 模块关系图
```text
    +-----------------+
    |  app (主模块)   |
    +-----------------+
      /   |    |    \
     v    v    v     v
biometric_auth  speech_recognition  image_analysis  push_notification  battery_monitor
```

## 开发环境要求
- Android Studio 2022.3.1 或更高版本
- JDK 17
- Android SDK 34

## 构建和运行说明
1. 克隆项目到本地
2. 打开Android Studio，导入项目
3. 连接Android设备或启动模拟器
4. 点击Run按钮构建并运行项目

