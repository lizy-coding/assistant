import android.graphics.Bitmap
import com.aliyun.tea.TeaException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object OcrService {
    suspend fun <T : Any> recognize(
        bitmap: Bitmap,
        model: OcrModel,
        config: OcrConfig,
        onError: (String) -> Unit
    ): Result<T> = when (model) {
        OcrModel.BANK_CARD -> processBankCard(bitmap, config as OcrConfig.BankCardConfig, onError) as Result<T>
        OcrModel.GENERAL_TEXT -> processText(bitmap, config as OcrConfig.TextConfig, onError) as Result<T>
    }

    private suspend fun processBankCard(
        bitmap: Bitmap,
        config: OcrConfig.BankCardConfig,
        onError: (String) -> Unit
    ): Result<BankCardResult> = safeApiCall(
        {
            val processed = bitmap
                .autoRotate()
                .apply { if (config.enableCardCrop) cropCardArea() }
                .adjustContrast(1.3f)

            val byteArray = processed.toValidatedJpeg(
                maxSizeMB = config.maxSizeMB,
                quality = config.minQuality
            )

            AliyunClient.recognizeBankCard(byteArray)
        },
        onError
    )

    private suspend fun processText(
        bitmap: Bitmap,
        config: OcrConfig.TextConfig,
        onError: (String) -> Unit
    ): Result<TextRecognitionResult> = safeApiCall(
        {
            val processed = bitmap
                .autoRotate()
                .scaleToMinHeight(config.minTextHeight)
                .binarize()

            val byteArray = processed.toValidatedJpeg(
                maxSizeMB = config.maxSizeMB,
                quality = config.minQuality
            )

            AliyunClient.recognizeText(byteArray, config)
        },
        onError
    )

    // Unified safe API call wrapper
    private suspend inline fun <T> safeApiCall(
        crossinline block: suspend () -> T,
        crossinline onError: (String) -> Unit
    ): Result<T> = try {
        Result.success(block())
    } catch (e: Exception) {
        val msg = when (e) {
            is TeaException -> mapTeaError(e)
            is ImageProcessingException -> e.userMessage
            else -> "识别服务暂不可用"
        }
        withContext(Dispatchers.Main) { onError(msg) }
        Result.failure(e)
    }

    private fun mapTeaError(e: TeaException): String = when (e.code) {
        "InvalidImage.Content" -> "请拍摄清晰的证件照片"
        "InvalidImage.Size" -> "图片尺寸不符合要求"
        else -> "识别服务错误（${e.code}）"
    }
}

// Define missing methods and classes
fun Bitmap.adjustContrast(contrast: Float): Bitmap {
    // Implement contrast adjustment logic
    return this
}

fun Bitmap.scaleToMinHeight(minHeight: Int): Bitmap {
    // Implement scaling logic
    return this
}

fun Bitmap.toValidatedJpeg(maxSizeMB: Int, quality: Int): ByteArray {
    val outputStream = ByteArrayOutputStream()
    var currentQuality = quality
    var byteArray: ByteArray

    do {
        outputStream.reset()
        this.compress(Bitmap.CompressFormat.JPEG, currentQuality, outputStream)
        byteArray = outputStream.toByteArray()
        currentQuality -= 5
    } while (byteArray.size > maxSizeMB * 1024 * 1024 && currentQuality > 0)

    if (byteArray.size > maxSizeMB * 1024 * 1024) {
        throw IllegalArgumentException("Image size exceeds the maximum limit")
    }

    return byteArray
}

class ImageProcessingException(val userMessage: String) : Exception(userMessage)