package com.example.image_analysis.server

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.aliyun.ocr20191230.Client
import com.aliyun.ocr20191230.models.RecognizeBankCardAdvanceRequest
import com.aliyun.ocr20191230.models.RecognizeBankCardResponse
import com.aliyun.tea.TeaException
import com.aliyun.teautil.models.RuntimeOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class AliyunOcrHelper(private val context: Context) {
    private val endpoint = "ocr.cn-shanghai.aliyuncs.com"
    private val client by lazy {
        try {
            Client(
                com.aliyun.teaopenapi.models.Config().apply {
                     accessKeyId = com.example.image_analysis.BuildConfig.ALIYUN_ACCESS_KEY_ID.trim()
                     accessKeySecret = com.example.image_analysis.BuildConfig.ALIYUN_ACCESS_KEY_SECRET.trim()
                    endpoint = this@AliyunOcrHelper.endpoint
                    // 修正2：添加必要协议配置
                    protocol = "HTTPS"
                    readTimeout = 30000
                }
            )
        } catch (e: Exception) {
            Log.e("Aliyun", "Client初始化失败: ${e.stackTraceToString()}")
            throw e
        }
    }


    suspend fun recognizeBankCardFromUri(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("Invalid image URI")

                val request = RecognizeBankCardAdvanceRequest().apply {
                    imageURLObject = inputStream
                }

                val response = client.recognizeBankCardAdvance(request, RuntimeOptions())
                parseBankCardResponse(response)
            } catch (e: TeaException) {
                handleAliyunError(e)
                ""
            }
        }
    }

    private fun parseBankCardResponse(response: RecognizeBankCardResponse): String {
        return response.body.data?.cardNumber ?: "No card number detected"
    }

    private fun handleAliyunError(e: TeaException) {
        Log.e("AliyunOcrHelper", "Error: ${e.message}, Code: ${e.code}")
        when (e.code) {
            "InvalidAccessKeyId.NotFound" -> throw SecurityException("AccessKey error")
            else -> throw RuntimeException("OCR failed: ${e.message}")
        }
    }

    // Bitmap recognition method
    suspend fun recognizeBankCardFromBitmap(bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            try {
                val byteArray = bitmap.toJpegByteArray().also {
                    Log.d("ImageData", "图片大小: ${it.size}字节")
                }

                ByteArrayInputStream(byteArray).use { inputStream ->
                    val request = RecognizeBankCardAdvanceRequest().apply {
                        imageURLObject = inputStream
                    }
                    val runtimeOptions = RuntimeOptions().apply {
                        connectTimeout = 30000
                        readTimeout = 30000
//                        key = mapOf(
//                            "Content-Type" to "application/octet-stream"
//                        )
                    }


                    // 正确获取响应对象
                    val response = client.recognizeBankCardAdvance(
                        request,
                        runtimeOptions

                    )

                    Log.d("Aliyun", "响应码: ${response.statusCode}")

                    // 从响应体中提取卡号
                    return@withContext response.body.data?.cardNumber
                        ?: throw IllegalStateException("未找到银行卡号")
                }
            } catch (e: TeaException) {
                val errorInfo = """
                API错误:
                Code: ${e.code}
                Message: ${e.message}
                RequestId: ${e.data?.get("RequestId")}
            """.trimIndent()
                Log.e("Aliyun", errorInfo)
                throw RuntimeException("OCR识别失败: ${e.code}")
            } catch (e: Exception) {
                Log.e("Aliyun", "全局异常: ${e.stackTraceToString()}")
                throw e
            }
        }
    }

    // Convert Bitmap to JPEG byte array
    private fun Bitmap.toJpegByteArray(quality: Int = 90): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    fun readJsonFromFile(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            Log.e("JsonReader", "Error reading JSON file", e)
            null
        }
    }


}