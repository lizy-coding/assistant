//package com.example.image_analysis
//
//import android.util.Log
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class ByteDanceOcrService(private val client: Client) {
//
//    suspend fun classifyImage(base64Image: String): String {
//        return withContext(Dispatchers.IO) { // Switch to IO context for network operations
//            try {
//                // Create request object
//                val request = ClassifyingRubbishRequest().setImageURL(base64Image)
//                val options = RuntimeOptions()
//
//                // Call API
//                val response = client.classifyingRubbishWithOptions(request, options)
//
//                // Process the response
//                Log.d("com.example.image_analysis.server.OcrService", "Response: ${com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(response))}")
//                response.toString() // Process according to the actual return type
//            } catch (error: TeaException) {
//                // Error handling
//                Log.e("com.example.image_analysis.server.OcrService", "TeaException: ${error.message}, Code: ${error.code}")
//                "Error: ${error.message}"
//            } catch (e: Exception) {
//                // Other exception handling
//                Log.e("com.example.image_analysis.server.OcrService", "Exception: ${e.message}", e) // Log the stack trace
//                "Error: ${e.message}"
//            }
//        }
//    }
//}