import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.aliyun.imagerecog20190930.Client
import com.aliyun.imagerecog20190930.models.ClassifyingRubbishRequest
import com.aliyun.teautil.models.RuntimeOptions
import com.aliyun.tea.TeaException
import com.aliyun.tea.TeaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageParser {

    private val client: Client = createClient()

    suspend fun parseImage(bitmap: Bitmap): String {
        val base64String = encodeBitmapToBase64(bitmap)
        return classifyImage(base64String)
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private suspend fun classifyImage(base64Image: String): String {
        return withContext(Dispatchers.IO) { // Switch to IO context for network operations
            try {
                // Create request object
                val request = ClassifyingRubbishRequest().setImageURL(base64Image)
                val options = RuntimeOptions()

                // Call API
                val response = client.classifyingRubbishWithOptions(request, options)

                // Process the response
                Log.d("ImageParser", "Response: ${com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(response))}")
                response.toString() // Process according to the actual return type
            } catch (error: TeaException) {
                // Error handling
                Log.e("ImageParser", "TeaException: ${error.message}, Code: ${error.code}")
                "Error: ${error.message}"
            } catch (e: Exception) {
                // Other exception handling
                Log.e("ImageParser", "Exception: ${e.message}", e) // Log the stack trace
                "Error: ${e.message}"
            }
        }
    }

    private fun createClient(): Client {
        val config = com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(System.getenv("ALIYUN_ACCESS_KEY_ID"))
            .setAccessKeySecret(System.getenv("ALIYUN_ACCESS_KEY_SECRET"))
            .setEndpoint("imagerecog.cn-shanghai.aliyuncs.com")
        return Client(config)
    }
}