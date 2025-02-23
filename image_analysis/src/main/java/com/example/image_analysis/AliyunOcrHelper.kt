package com.example.image_analysis

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.aliyun.ocr20191230.Client
import com.aliyun.ocr20191230.models.RecognizeCharacterRequest
import com.aliyun.ocr20191230.models.RecognizeCharacterResponse
import com.aliyun.tea.TeaException
import com.aliyun.teautil.models.RuntimeOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AliyunOcrHelper(private val context: Context) {
    private val client: Client = createClient()

    private fun createClient(): Client {
        val accessKeyId = BuildConfig.ALIYUN_ACCESS_KEY_ID.trim()
        val accessKeySecret = BuildConfig.ALIYUN_ACCESS_KEY_SECRET.trim()
        Log.i("AliyunOcrHelper", "accessKeyId=$accessKeyId, accessKeySecret=$accessKeySecret")
        val config = com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret)
            .setEndpoint("imagerecog.cn-shanghai.aliyuncs.com")
        return Client(config)
    }

    suspend fun recognizeLocalImage(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("Invalid image URI")

                val byteArray = inputStream.readBytes()
                val base64Image = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
                val request = RecognizeCharacterRequest().apply {
                    imageURL = "data:image/jpeg;base64,$base64Image"
                }

                val response = client.recognizeCharacterWithOptions(request, RuntimeOptions())
                parseOcrResponse(response)
            } catch (e: TeaException) {
                handleAliyunError(e)
                ""
            }
        }
    }

    private fun parseOcrResponse(response: RecognizeCharacterResponse): String {
        return response.body.data?.results
            ?.mapNotNull { it.text }
            ?.joinToString("\n")
            ?: throw RuntimeException("No text detected")
    }

    private fun handleAliyunError(e: TeaException) {
        when (e.code) {
            "InvalidAccessKeyId.NotFound" -> throw SecurityException("AccessKey error")
            else -> throw RuntimeException("OCR failed: ${e.message}")
        }
    }

    // Bitmap recognition method
    suspend fun recognizeBitmap(bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            try {
                val byteArray = bitmap.toJpegByteArray()
                val base64Image = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
                val request = RecognizeCharacterRequest().apply {
                    imageURL = "data:image/jpeg;base64,$base64Image"
                }

                val response = client.recognizeCharacterWithOptions(request, RuntimeOptions())
                parseOcrResponse(response)
            } catch (e: TeaException) {
                handleAliyunError(e)
                ""
            }
        }
    }

    // Convert Bitmap to JPEG byte array
    private fun Bitmap.toJpegByteArray(quality: Int = 90): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
}