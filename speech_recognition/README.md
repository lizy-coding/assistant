# 语音识别模块

本模块提供Android平台上的语音识别功能，支持实时语音转文字。

## 功能特性
- 支持多种语音识别引擎
- 实时语音转文字
- 自定义识别配置

## 快速开始

### 1. 添加依赖

在模块的`build.gradle`中添加以下依赖：

```gradle
implementation project(":speech_recognition")
```

### 2. 初始化模块

在应用启动时或Activity/Fragment创建时初始化：

```kotlin
val recognizer = SpeechRecognizer.create(context)
recognizer.init()
```

### 3. 使用语音识别

```kotlin
recognizer.startListening(object : RecognitionListener {
    override fun onResults(results: List<String>) {
        // 处理识别结果
    }
})
```

## API参考

- `SpeechRecognizer`: 核心识别类
- `RecognitionListener`: 识别结果回调接口
- `RecognitionConfig`: 识别配置类