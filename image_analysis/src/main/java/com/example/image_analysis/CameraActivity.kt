package com.example.image_analysis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    
    private val TAG = "CameraActivity"
    
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    
    private lateinit var viewFinder: PreviewView
    private lateinit var captureButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var backButton: Button
    private lateinit var previewImageView: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        
        // 初始化视图
        viewFinder = findViewById(R.id.viewFinder)
        captureButton = findViewById(R.id.camera_capture_button)
        resultTextView = findViewById(R.id.text_result)
        backButton = findViewById(R.id.button_back)
        previewImageView = findViewById(R.id.preview_image)
        
        // 检查权限
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        
        // 设置拍照按钮点击事件
        captureButton.setOnClickListener { takePhoto() }
        
        // 设置返回按钮点击事件
        backButton.setOnClickListener { finish() }
        
        // 检查是否有图像 URI 传入
        intent.getParcelableExtra<Uri>("IMAGE_URI")?.let { uri ->
            // 如果有传入图像，直接进行分析
            previewImageView.setImageURI(uri)
            viewFinder.visibility = android.view.View.GONE
            previewImageView.visibility = android.view.View.VISIBLE
            captureButton.visibility = android.view.View.GONE
            
            // 执行图像分析
            analyzeImage(uri)
        }
        
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }
    
    private fun takePhoto() {
        Log.d(TAG, "拍照")
        
        // 获取 imageCapture 实例
        val imageCapture = imageCapture ?: return
        
        // 创建带时间戳的图片文件
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg")
        
        // 输出选项
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        // 拍照
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "拍照失败: ${exc.message}", exc)
                }
                
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d(TAG, "照片保存在: $savedUri")
                    
                    // 显示拍摄的图片
                    viewFinder.visibility = android.view.View.GONE
                    previewImageView.visibility = android.view.View.VISIBLE
                    previewImageView.setImageURI(savedUri)
                    captureButton.visibility = android.view.View.GONE
                    
                    // 分析图像
                    analyzeImage(savedUri)
                }
            })
    }
    
    private fun analyzeImage(imageUri: Uri) {
        // 在这里执行图像分析算法
        // 这里仅作为演示，显示一个简单的结果
        val result = "图像识别结果: 分析中...\n(这里是模拟结果)"
        resultTextView.text = result
        
        // 模拟延迟，实际应用中应该是真实的图像处理
        resultTextView.postDelayed({
            val finalResult = "图像识别结果:\n- 检测到对象: 人脸(85%)\n- 情绪: 微笑(72%)\n- 其他特征: 眼镜(94%)"
            resultTextView.text = finalResult
            
            // 设置返回结果
            val resultIntent = Intent()
            resultIntent.putExtra("RECOGNITION_RESULT", finalResult)
            resultIntent.putExtra("IMAGE_URI", imageUri)
            setResult(RESULT_OK, resultIntent)
            
        }, 2000) // 模拟2秒的处理时间
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            // 绑定生命周期
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            
            // 创建预览
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            
            // 设置图像捕获
            imageCapture = ImageCapture.Builder()
                .build()
            
            // 选择后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                // 解绑所有用例
                cameraProvider.unbindAll()
                
                // 绑定用例
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
                
            } catch(exc: Exception) {
                Log.e(TAG, "绑定用例失败", exc)
            }
            
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    
    // 创建输出目录
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "权限未授予，无法使用相机功能。",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    
    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

