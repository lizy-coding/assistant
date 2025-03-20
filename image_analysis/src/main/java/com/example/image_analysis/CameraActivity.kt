package com.example.image_analysis

import CameraHandler
import OcrConfig
import OcrModel
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.example.image_analysis.databinding.ActivityCameraBinding
import com.example.image_analysis.server.OcrService
import com.example.image_analysis.server.TextRecognitionResult
import kotlinx.coroutines.launch
import java.io.File

class CameraActivity : AppCompatActivity() {
    private val cameraTag = "CameraActivity"
    private lateinit var cameraHandler: CameraHandler
    private lateinit var previewView: PreviewView
    private lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        binding = ActivityCameraBinding.inflate(layoutInflater) // 初始化绑定
        setContentView(binding.root) // 必须调用
        // 初始化 PreviewView
        previewView = findViewById(R.id.previewView)

        // 初始化 CameraHandler
        cameraHandler = CameraHandler(this)

        val captureButton: Button = findViewById(R.id.captureButton)
        captureButton.setOnClickListener {
            // 拍照并保存
            startCameraCapture()
        }

        previewView.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_MOVE -> {
                    if (event.x < 0) {
                        finish() // 退出到首页
                    }
                }

                android.view.MotionEvent.ACTION_DOWN -> {
                    view.performClick() // 调用 performClick() 以处理可访问性
                }
            }
            false
        }
    }

//    private fun startCameraCapture() {
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(
//            CameraHandler.getOutputFile(this)
//        ).build()
//        cameraController.takePicture(
//            outputOptions,
//            cameraExecutor,
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    CameraHandler.getBitmap(outputFileResults.savedUri!!) { bitmap ->
//                        CoroutineScope(Dispatchers.Main).launch {
//                            try {
//                                val result: Result<TextRecognitionResult> = OcrService.recognize(
//                                    bitmap,
//                                    OcrModel.GENERAL_TEXT,
//                                    OcrConfig.TextConfig(),
//                                    onError = { error ->
//                                        Log.e(cameraTag, "Error during text recognition: $error")
//                                        // Handle the error, e.g., show a message to the user
//                                    }
//                                )
//                                result.onSuccess { textRecognitionResult ->
//                                    Log.e(cameraTag, "recognizeBitmap.result=$textRecognitionResult")
//                                    // Process the successful result
//                                    Log.d("startCameraCapture.OcrService.recognize.processText.recognizeText", "data=$textRecognitionResult")
//                                }.onFailure { exception ->
//                                    Log.e(cameraTag, "Error during text recognition: ${exception.message}")
//                                    // Handle the failure, e.g., show an error message
//                                }
//                            } catch (e: Exception) {
//                                Log.e(cameraTag, "Unexpected error: ${e.message}", e)
//                                // Handle unexpected exceptions
//                            }
//                        }
//                    }
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    Log.e(cameraTag, "Image capture failed: ${exception.message}", exception)
//                    // Handle image capture errors
//                }
//            })
//    }

    fun getLayoutId(): Int {
        return R.layout.activity_camera
    }

    private fun startCameraCapture() {
        // 获取存储路径（相册目录）
//        val storageDir = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//            "AssistantPhoto"
//        )
        val storageDir = File(
            getExternalFilesDir("recognize"),
            "AssistantPhoto"
        )
        Log.e("startCameraCapture", "storageDir=$storageDir")

        // 创建目录（如果不存在）
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }


        cameraHandler.startCameraCapture(previewView, storageDir) { bitmap ->
            // 处理返回的 Bitmap
            lifecycleScope.launch {
                val ocrServer = OcrService

                // 显示加载状态
                binding.progressBar.visibility = View.VISIBLE
                // 执行OCR识别
                val result: Result<TextRecognitionResult> =
                    ocrServer.recognize<TextRecognitionResult>(
                        bitmap = bitmap,
                        model = OcrModel.GENERAL_TEXT,
                        config = OcrConfig.TextConfig(
                            minTextHeight = 10,  // Example value
                            maxSizeMB = 3.0,    // Example value
                            minQuality = 80      // Example value
                        ),
                        onError = { errorMessage ->
                            Log.e(
                                "startCameraCapture",
                                "Error occurred: $errorMessage"
                            )
                        }
                    )
                val noData = "noResult"
                Log.e(
                    "startCameraCapture",
                    "recognizeBitmap.result=${result.isSuccess} ${result.isFailure}  ${result.getOrNull() ?: noData}"
                )
                // 显示结果
                binding.ocrResultText.text = result.getOrNull().toString()
                Toast.makeText(
                    this@CameraActivity,
                    "识别成功：${result}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

