# 图像分析模块

本模块提供Android平台上的图像分析功能，支持图像识别和处理。

## 功能特性
- 支持多种图像处理算法
- 实时图像分析
- 自定义分析配置

## 快速开始

### 1. 添加依赖

在模块的`build.gradle`中添加以下依赖：

```gradle
implementation project(":image_analysis")
```

### 2. 初始化模块

在应用启动时或Activity/Fragment创建时初始化：

```kotlin
val analyzer = ImageAnalyzer.create(context)
analyzer.init()
```

### 3. 使用图像分析

```kotlin
analyzer.analyze(image, object : AnalysisListener {
    override fun onResults(results: List<String>) {
        // 处理分析结果
    }
})
```

## API参考

- `ImageAnalyzer`: 核心分析类
- `AnalysisListener`: 分析结果回调接口
- `AnalysisConfig`: 分析配置类