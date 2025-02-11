import android.graphics.Bitmap
import android.util.Base64

class ImageParser {

    suspend fun parseImage(bitmap: Bitmap): String {
        val base64String = encodeBitmapToBase64(bitmap)
        val response = RetrofitInstance.api.analyzeImage(ImageRequest(base64String))
        return response.result
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
