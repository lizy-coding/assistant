package com.example.image_analysis.server

import OcrConfig
import android.graphics.BitmapFactory
import android.util.Log
import com.aliyun.ocr20191230.Client
import com.aliyun.ocr20191230.models.RecognizeBankCardAdvanceRequest
import com.aliyun.ocr20191230.models.RecognizeCharacterAdvanceRequest
import com.aliyun.ocr20191230.models.RecognizeCharacterResponse
import com.aliyun.tea.TeaException
import com.aliyun.tea.TeaRetryableException
import com.aliyun.tea.TeaUnretryableException
import com.aliyun.teaopenapi.models.Config
import com.aliyun.teautil.models.RuntimeOptions
import com.example.image_analysis.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

object AliyunClient {
    private val client = OkHttpClient()

    object OCRClient {
        private const val REGION_ID = "cn-shanghai"
        val instance: Client by lazy {
            Config().apply {
                accessKeyId = BuildConfig.ALIYUN_ACCESS_KEY_ID
                accessKeySecret = BuildConfig.ALIYUN_ACCESS_KEY_SECRET
                regionId = REGION_ID
                // 启用自动重试
            }.let {
                Client(it)
            }
        }
    }


    // 银行卡识别
    fun recognizeBankCard(imageData: ByteArray): BankCardResult {
        return try {
            ByteArrayInputStream(imageData).use { stream ->
                val request = RecognizeBankCardAdvanceRequest().apply {
                    imageURLObject = stream
                }

                // 使用方式
                OCRClient.instance.recognizeBankCardAdvance(
                    request,
                    RuntimeOptions()
                ).body.data.let {
                    BankCardResult(
                        cardNumber = it?.cardNumber ?: "",
                        validDate = it?.validDate ?: "",
                        bankName = it?.bankName,
                        cardType = it?.cardType
                    )
                }
            }
        } catch (e: TeaException) {
            handleTeaException(e)
            BankCardResult.EMPTY
        } catch (e: Exception) {
            Log.d("startCameraCapture", "Unexpected error: ${e.message}")
            BankCardResult.EMPTY
        }
    }

    // 文字识别
    suspend fun recognizeText(imageData: ByteArray, config: OcrConfig.TextConfig): TextRecognitionResult {
        if (!isValidImage(imageData)) {
            Log.e("OCR", "Invalid image format/size")
            return TextRecognitionResult.EMPTY
        }
        Log.d("OCR", "Sending request to Aliyun OCR: config=$config, ImageData size: ${imageData.size}");
        val file = File("/storage/emulated/0/Android/data/com.example.assistant/files/recognize/AssistantPhoto/", "test_image.jpg")
        file.writeBytes(imageData)
        Log.d("OCR", "Saved image for debugging: ${file.absolutePath}")

              var stream: InputStream? = null
        return try                                        {
//            stream = ByteArrayInputStream(imageData)

            // 修正 1：使用 URL 而非流


            stream = getImageInputStreamFromUrl("https://www.bing.com/images/search?view=detailV2&ccid=naJcXN%2FK&id=3C460E13552BE9786E7AD5770743770DBB4194BE&thid=OIP.naJcXN_KHVDUVuc46x4VMQHaLH&mediaurl=https%3A%2F%2Fimg.tukuppt.com%2Fad_preview%2F01%2F38%2F98%2F634e50d22c112.jpg!%2Ffw%2F780&exph=1170&expw=780&q=%E5%9B%BE%E7%89%87&simid=608048167624927386&form=IRPRST&ck=6A89077F2766845882B9C7B3D841C64A&selectedindex=11&itb=0&cw=1721&ch=869&ajaxhist=0&ajaxserp=0&vt=0&sim=11")
            Log.e("OCR", "stream: ${stream.toString()}")
            val request = RecognizeCharacterAdvanceRequest().apply {
               /// 使用链式调用保证参数正确注入
                setImageURLObject(stream)
//                setMinHeight(config.minTextHeight)
                setOutputProbability(config.outputProbability)

                // ⚠️ 修正 1：改用 URL 而非流
//                val imageUrl = uploadToOSS(imageData)
//                setImageURL(imageUrl)
//
//                // ⚠️ 修正 2：恢复 MinHeight 传参
//                setMinHeight(config.minTextHeight)
//
//                // ⚠️ 修正 3：确保 OutputProbability 传参正确
//                setOutputProbability(config.outputProbability ?: false)
            }

            // 添加运行时配置（重要！）
            val runtime = RuntimeOptions().apply {
                connectTimeout = 10000 // 10秒连接超时
                readTimeout = 15000    // 15秒读取超时
            }

            // 使用单例客户端发送请求
            val response = OCRClient.instance.recognizeCharacterAdvance(request, runtime)
            parseOCRResponse(response)
        } catch (e: TeaException) {
            // 处理阿里云SDK特有异常
            Log.e("OCR", "API Error: [${e.code}] ${e.message}", e)
            when (e.code) {
                "ServiceUnavailable" -> TextRecognitionResult.ERROR_SERVICE
                else -> TextRecognitionResult.ERROR_UNKNOWN
            }
        } catch (e: IOException) {
            // 处理网络和IO异常
            Log.e("OCR", "Network Error: ${e.javaClass.simpleName}", e)
            TextRecognitionResult.ERROR_NETWORK
        } finally {
            // 确保流资源释放
            stream?.close()
        }
    }


    private fun handleTeaException(e: Throwable) {
        when (e) {
            is TeaUnretryableException -> {

                Log.d("AliyunClient", "不可重试异常，原始错误响应: ${e.message}")
//                e.rawResponse?.let {
//                    Log.d("AliyunClient", "原始错误响应: ${String(it)}")
//                }
            }

            is TeaRetryableException -> {

                Log.w("AliyunClient", "可重试异常，原始错误响应: $e")
            }

            else -> {
                Log.e("AliyunClient", "未分类异常", e)
            }
        }
    }


    private fun isValidImage(data: ByteArray): Boolean {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
            // 检查格式和尺寸
            when (options.outMimeType) {
                "image/jpeg", "image/png" -> {}
                else -> return false
            }
            data.size <= 3 * 1024 * 1024 // 不超过3MB
        } catch (e: Exception) {
            false
        }
    }
    private suspend fun getImageInputStreamFromUrl(imageUrl: String): InputStream? =
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(imageUrl).build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    return@withContext response.body?.byteStream()
                } else {
                    Log.e("getImageInputStreamFromUrl", "HTTP error: ${response.code} ${response.message}")
                    return@withContext null
                }
            } catch (e: IOException) {
                Log.e("getImageInputStreamFromUrl", "Error fetching image: ${e.message}", e)
                return@withContext null
            }
        }

}

private fun parseOCRResponse(response: RecognizeCharacterResponse): TextRecognitionResult {
    return try {
        val body = response.body ?: return TextRecognitionResult.EMPTY
        val results = body.data?.results ?: return TextRecognitionResult.EMPTY

        TextRecognitionResult(
            textBlocks = results.mapNotNull { result ->
                result.text?.let { text ->
                    TextBlock(
                        text = text,
                        confidence = result.probability ?: 0f,
                        boundingBox = result.textRectangles?.let { rect ->
                            TextRectangle(
                                left = rect.left?.toInt() ?: 0,
                                top = rect.top?.toInt() ?: 0,
                                width = rect.width?.toInt() ?: 0,
                                height = rect.height?.toInt() ?: 0
                            )
                        } ?: TextRectangle.EMPTY
                    )
                }
            },
            requestId = body.requestId
        )
    } catch (e: Exception) {
        Log.e("OCR", "Parse error: ${e.stackTraceToString()}")
        TextRecognitionResult.EMPTY
    }
}

// 增强数据结构定义
data class TextRecognitionResult(
    val textBlocks: List<TextBlock>,
    val requestId: String? = null  // 新增请求ID跟踪
) {
    companion object {
        val EMPTY = TextRecognitionResult(emptyList(), "")
        val ERROR_UNKNOWN = TextRecognitionResult(emptyList(), "ERROR_UNKNOWN")
        val ERROR_SERVICE = TextRecognitionResult(emptyList(), "ERROR_SERVICE")
        val ERROR_NETWORK = TextRecognitionResult(emptyList(), "ERROR_NETWORK")
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Request ID: ").append(requestId ?: "null").append("\n")
        stringBuilder.append("Text Blocks:\n")
        textBlocks.forEachIndexed { index, textBlock ->
            stringBuilder.append("  Text Block ").append(index + 1).append(":\n")
            stringBuilder.append("    ").append(textBlock.toString()).append("\n")
        }
        return stringBuilder.toString()
    }
}

data class TextBlock(
    val text: String,
    val confidence: Float,
    val boundingBox: TextRectangle
)

data class TextRectangle(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int
) {
    companion object {
        val EMPTY = TextRectangle(0, 0, 0, 0)
    }
}

    // 银行卡识别结果结构
    data class BankCardResult(
        val cardNumber: String,
        val validDate: String,
        val bankName: String? = null,
        val cardType: String? = null
    ) {
        init {
            require(cardNumber.isEmpty() || cardNumber.matches("\\d{13,19}".toRegex())) {
                "无效的银行卡号格式"
            }
        }

        companion object {
            val EMPTY = BankCardResult(
                cardNumber = "",
                validDate = ""
            )
        }
    }
