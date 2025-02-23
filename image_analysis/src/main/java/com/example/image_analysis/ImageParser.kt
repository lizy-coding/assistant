//package com.example.image_analysis
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.util.Base64
//import android.util.Log
//import com.aliyun.imagerecog20190930.Client
//import com.aliyun.imagerecog20190930.models.ClassifyingRubbishRequest
//import com.aliyun.teautil.models.RuntimeOptions
//import com.aliyun.tea.TeaException
//import com.aliyun.tea.TeaModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.io.ByteArrayOutputStream
//
//class ImageParser {
//
//    private lateinit var aliOcrClient: ALIOcrClient
//     suspend fun parseImage(bitmap: Bitmap): String {
//        val compressedBitmap = compressBitmap(bitmap)
//        val base64String = encodeBitmapToBase64(compressedBitmap)
//        return aliOcrClient.classifyImage(base64String)
//    }
//
////    压缩图片
//    private fun compressBitmap(bitmap: Bitmap): Bitmap {
//        // Compress the bitmap to ensure it is under 3 MB
//        val maxFileSize = 3 * 1024 * 1024 // 3 MB
//        var quality = 100
//        var stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
//
//        // Reduce quality until the size is under the limit
//        while (stream.toByteArray().size > maxFileSize && quality > 0) {
//            stream.reset() // Clear the stream
//            quality -= 10 // Decrease quality
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
//        }
//
//        // Convert the stream back to a Bitmap
//        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
//    }
//
//    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//        val byteArray = byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
//    }
//
////    private suspend fun classifyImage(base64Image: String): String {
////        return withContext(Dispatchers.IO) { // Switch to IO context for network operations
////            try {
////                // Create request object
////                val request = ClassifyingRubbishRequest().setImageURL(base64Image)
////                val options = RuntimeOptions()
////
////                // Call API
////                val response = client.classifyingRubbishWithOptions(request, options)
////
////                // Process the response
////                Log.d("ImageParser", "Response: ${com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(response))}")
////                response.toString() // Process according to the actual return type
////            } catch (error: TeaException) {
////                // Error handling
////                Log.e("ImageParser", "TeaException: ${error.message}, Code: ${error.code}")
////                "Error: ${error.message}"
////            } catch (e: Exception) {
////                // Other exception handling
////                Log.e("ImageParser", "Exception: ${e.message}", e) // Log the stack trace
////                "Error: ${e.message}"
////            }
////        }
////    }
////
////    private fun createClient(): Client {
////        val accessKeyId = BuildConfig.ALIYUN_ACCESS_KEY_ID.trim()
////        val accessKeySecret = BuildConfig.ALIYUN_ACCESS_KEY_SECRET.trim()
////        Log.i("ImageParser", "accessKeyId=$accessKeyId,  accessKeySecret=$accessKeySecret")
////        val config = com.aliyun.teaopenapi.models.Config()
////            .setAccessKeyId(accessKeyId)
////            .setAccessKeySecret(accessKeySecret)
////            .setEndpoint("imagerecog.cn-shanghai.aliyuncs.com")
////        return Client(config)
////    }
//}