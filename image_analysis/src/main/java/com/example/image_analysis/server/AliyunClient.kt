import android.graphics.Rect
import com.aliyun.ocr20191230.Client
import com.aliyun.ocr20191230.models.RecognizeBankCardAdvanceRequest
import com.aliyun.ocr20191230.models.RecognizeCharacterAdvanceRequest
import com.aliyun.tea.TeaException
import com.aliyun.teautil.models.RuntimeOptions
import java.io.ByteArrayInputStream

object AliyunClient {
    private val client by lazy {
        Client(
            com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(System.getenv("ALIYUN_ACCESS_KEY_ID") ?: "your-access-key")
                .setAccessKeySecret(System.getenv("ALIYUN_ACCESS_KEY_SECRET") ?: "your-secret-key")
                .setEndpoint("ocr-api.cn-hangzhou.aliyuncs.com")
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
            BankCardResult("", "")
        } catch (e: Exception) {
            println("Unexpected error: ${e.message}")
            BankCardResult("", "")
        }
    }

    // 文字识别
    fun recognizeText(imageData: ByteArray, config: OcrConfig.TextConfig): TextRecognitionResult {
        return try {
            ByteArrayInputStream(imageData).use { stream ->
                val request = RecognizeCharacterAdvanceRequest().apply {
                    imageURLObject = stream
                    minHeight = config.minTextHeight
                    outputProbability = config.outputProbability
                }

                client.recognizeCharacterAdvance(request, RuntimeOptions()).body.data.let { data ->
                    val textRegions = data?.results?.map { result ->
                        val rect = result.textRectangles
                        TextRecognitionResult.TextRegion(
                            text = result.text,  // Adjust field name based on actual result structure
                            probability = result.probability,  // Adjust field name based on actual result structure
                            boundingBox = Rect(
                                rect.left,
                                rect.top,
                                rect.left + rect.width,
                                rect.top + rect.height
                            )
                        )
                    } ?: emptyList()

                    TextRecognitionResult(
                        content = textRegions.joinToString(" ") { it.text },
                        confidence = textRegions.map { it.probability }.average(),
                        textRegions = textRegions
                    )
                }
            }
        } catch (e: TeaException) {
            handleTeaException(e)
            TextRecognitionResult.EMPTY
        } catch (e: Exception) {
            println("Unexpected error: ${e.message}")
            TextRecognitionResult.EMPTY
        }
    }



    private fun handleTeaException(e: TeaException) {
        // Log the error and handle it appropriately
        println("Error: ${e.message}, Code: ${e.code}")
    }
}

// 文字识别结果结构
data class TextRecognitionResult(
    val content: String,
    val confidence: Double,
    val textRegions: List<TextRegion>
) {
    data class TextRegion(
        val text: String,
        val probability: Float,
        val boundingBox: Rect
    )

    companion object {
        val EMPTY = TextRecognitionResult(
            content = "",
            confidence = 0.0,
            textRegions = emptyList()
        )
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
}