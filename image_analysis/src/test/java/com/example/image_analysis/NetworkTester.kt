package com.example.image_analysis

import org.junit.Test
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

// 添加网络诊断工具类
class NetworkTester {
    @Test
    fun testOCREndpoint() {
        val url = URL("https://ocr.cn-shanghai.aliyuncs.com")
        val connection = url.openConnection() as HttpsURLConnection
        try {
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val code = connection.responseCode
            TestLogger.d("NETWORK", "Endpoint可达性测试: HTTP $code")
        } catch (e: IOException) {
            TestLogger.e("NETWORK", "无法连接OCR服务端点: ${e.javaClass.simpleName}")
        } finally {
            connection.disconnect()
        }
    }
}

// app/src/test/java/com/example/image_analysis/TestLogger.kt
object TestLogger {
    fun e(tag: String, msg: String) = println("ERROR/$tag: $msg")
    fun d(tag: String, msg: String) = println("DEBUG/$tag: $msg")
}