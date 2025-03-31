# 生物识别模块

这是一个提供Android生物识别功能的模块，支持指纹、面容等生物特征验证。

## 模块架构

模块采用分包设计，包括以下几个部分：

- `api`：对外提供的API接口
- `core`：核心功能实现
- `aspect`：切面功能实现
- `annotation`：注解定义
- `utils`：工具类

## 快速开始

### 1. 添加依赖

在模块的`build.gradle`中添加以下依赖：

```gradle
implementation project(":biometric_auth")
```

### 2. 初始化模块

在应用启动时或Activity/Fragment创建时初始化：

```kotlin
// 简单初始化
val securedMethods = setOf("methodName1", "methodName2")
val config = BiometricAuth.createConfig(
    title = "验证标题",
    subtitle = "请进行生物识别验证",
    securedMethods = securedMethods
)
BiometricAuth.init("your_module_name", config)
```

### 3. 使用生物识别验证

#### 方式一：使用API直接验证

```kotlin
BiometricAuth.authenticate(
    activity = this,
    onSuccess = { 
        // 验证成功后执行的操作
    },
    onFailure = {
        // 验证失败处理
    },
    onError = { errorCode, errorMessage ->
        // 发生错误处理
    }
)
```

#### 方式二：使用切面验证方法

```kotlin
BiometricAuth.verifyMethod(
    activity = this,
    moduleName = "your_module_name",
    methodName = "methodName",
    onSuccess = {
        // 验证成功执行的业务逻辑
    },
    onError = { errorCode, errorMessage ->
        // 错误处理
    }
)
```

#### 方式三：使用注解

可以使用`@BiometricAuthentication`注解标记需要生物识别验证的方法（需配合AOP框架使用）。

```kotlin
@BiometricAuthentication
fun securedMethod() {
    // 需要受保护的方法
}
```

## 检测设备支持状态

在使用生物识别前，建议先检查设备是否支持：

```kotlin
if (BiometricAuth.isSupported(context)) {
    // 设备支持生物识别
} else {
    // 设备不支持，提供备选方案
}
``` 