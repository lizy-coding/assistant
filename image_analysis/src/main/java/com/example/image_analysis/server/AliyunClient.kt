package com.example.image_analysis.server

import OcrConfig
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import com.aliyun.ocr20191230.Client
import com.aliyun.ocr20191230.models.RecognizeBankCardAdvanceRequest
import com.aliyun.ocr20191230.models.RecognizeCharacterAdvanceRequest
import com.aliyun.tea.TeaException
import com.aliyun.tea.TeaRetryableException
import com.aliyun.tea.TeaUnretryableException
import com.aliyun.teaopenapi.models.Config
import com.aliyun.teautil.models.RuntimeOptions
import com.example.image_analysis.BuildConfig
import java.io.ByteArrayInputStream
import java.io.IOException

object AliyunClient {
    private val client by lazy {

        val accessKeyId = BuildConfig.ALIYUN_ACCESS_KEY_ID
        val accessKeySecret = BuildConfig.ALIYUN_ACCESS_KEY_SECRET

        Log.d("startCameraCapture:client", "ALIYUNACCESSKEYID=$accessKeyId, ALIYUNACCESSKEYIDSECRET=$accessKeySecret ")
        Client(
            Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("ocr.cn-shanghai.aliyuncs.com")
        )
    }

    // 银行卡识别
    fun recognizeBankCard(imageData: ByteArray): BankCardResult {
        return try {
            ByteArrayInputStream(imageData).use { stream ->
                val request = RecognizeBankCardAdvanceRequest().apply {
                    imageURLObject = stream
                }
                client.recognizeBankCardAdvance(request, RuntimeOptions()).body.data.let {
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
    fun recognizeText(imageData: ByteArray, config: OcrConfig.TextConfig): TextRecognitionResult {

        if (!isValidImage(imageData)) {
            Log.e("startCameraCapture OCR_VALIDATE", "Invalid image format/size")
            return TextRecognitionResult.EMPTY
        }
        return try {
            ByteArrayInputStream(imageData).use { stream ->
                val request = RecognizeCharacterAdvanceRequest().apply {
                    setImageURLObject(stream) // 使用正确的方法设置流
                    minHeight = config.minTextHeight
                    outputProbability = config.outputProbability
                }
                Log.d("startCameraCapture OCR_DEBUG", "Request: ${request.toMap()}")
                val response = client.recognizeCharacterAdvance(request, RuntimeOptions().apply {
                    readTimeout = 30000
                    connectTimeout = 10000
                })
                // 打印请求详情


                Log.d("startCameraCapture OCR_DEBUG", "response: ${response.toMap()}")

                response.body.data?.results?.mapNotNull { result ->
                    result.text?.let { text ->
                        TextRecognitionResult.TextRegion(
                            text = text,
                            probability = result.probability ?: 0.0f,
                            boundingBox = result.textRectangles?.let { rect ->
                                Rect(rect.left, rect.top, rect.left + rect.width, rect.top + rect.height)
                            } ?: Rect()
                        )
                    }
                }?.let { regions ->
                    TextRecognitionResult(
                        content = regions.joinToString(" ") { it.text },
                        confidence = regions.map { it.probability }.average(),
                        textRegions = regions
                    )
                } ?: TextRecognitionResult.EMPTY
            }
        } catch (e: com.aliyun.tea.TeaException) {
            handleTeaException(e)
            TextRecognitionResult.EMPTY
        } catch (e: IOException) {
            Log.e("OcrService", "IO异常", e)
            TextRecognitionResult.EMPTY
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

// 文字识别结果结构
data class TextRecognitionResult(
    val content: String,
    val confidence: Double,
    val textRegions: List<TextRegion>
) {
    companion object {
        val EMPTY = TextRecognitionResult(
            content = "",
            confidence = 0.0,
            textRegions = emptyList()
        )
    }

    data class TextRegion(
        val text: String,
        val probability: Float,
        val boundingBox: Rect
    )
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