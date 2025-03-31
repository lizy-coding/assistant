package com.example.image_analysis

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class ImageAnalysisDemoFragment : Fragment() {

    private val TAG = "ImageAnalysisDemoFragment"
    
    private lateinit var imagePreview: ImageView
    private lateinit var textResult: TextView
    
    // 通过接口与Activity通信的回调
    private var biometricCallback: BiometricCallback? = null
    
    // 用于接收相机 Activity 返回的结果
    private val cameraActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            // 处理从 CameraActivity 返回的识别结果
            val recognitionResult = data?.getStringExtra("RECOGNITION_RESULT")
            val imageUri = data?.getParcelableExtra<Uri>("IMAGE_URI")
            
            // 更新 UI 显示结果
            recognitionResult?.let {
                textResult.text = it
            }
            
            // 显示图像
            imageUri?.let {
                imagePreview.setImageURI(it)
            }
            
            Log.d(TAG, "从相机返回的图像识别结果: $recognitionResult")
        } else {
            Log.d(TAG, "相机活动未返回有效结果")
        }
    }
    
    // 用于接收从相册选择图片的结果
    private val galleryPickerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val selectedImageUri = data?.data
            
            selectedImageUri?.let {
                // 显示选中的图片
                imagePreview.setImageURI(it)
                
                // 使用回调请求图像分析
                if (biometricCallback != null) {
                    biometricCallback?.requestImageAnalysisWithBiometric(it)
                } else {
                    // 如果没有回调，使用本地方法进行验证和分析
                    verifyAndAnalyzeImage(it)
                }
            }
        }
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 尝试获取回调接口实现
        if (context is BiometricCallback) {
            biometricCallback = context
            Log.d(TAG, "成功获取BiometricCallback")
        } else {
            Log.w(TAG, "宿主Activity未实现BiometricCallback接口")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_analysis_demo, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化视图
        imagePreview = view.findViewById(R.id.image_preview)
        textResult = view.findViewById(R.id.text_result)
        
        // 设置打开相机按钮点击事件
        val btnOpenCamera = view.findViewById<Button>(R.id.btn_open_camera)
        btnOpenCamera.setOnClickListener {
            Log.d(TAG, "点击打开相机按钮")
            if (biometricCallback != null) {
                // 使用回调请求相机操作
                biometricCallback?.requestCameraWithBiometric()
            } else {
                // 如果没有回调，使用本地方法
                directOpenCamera()
            }
        }
        
        // 设置从相册选择图片按钮点击事件
        val btnSelectImage = view.findViewById<Button>(R.id.btn_select_image)
        btnSelectImage.setOnClickListener {
            Log.d(TAG, "点击从相册选择图片按钮")
            if (biometricCallback != null) {
                // 使用回调请求相册操作
                biometricCallback?.requestGalleryWithBiometric()
            } else {
                // 如果没有回调，使用本地方法
                verifyAndOpenGallery()
            }
        }
    }
    
    // 直接打开相机（当宿主Activity不实现BiometricCallback时的备用方案）
    private fun directOpenCamera() {
        try {
            // 创建启动 CameraActivity 的 Intent
            val intent = Intent(requireContext(), CameraActivity::class.java)
            cameraActivityResult.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "启动相机活动失败: ${e.message}")
            Toast.makeText(requireContext(), "无法启动相机功能", Toast.LENGTH_SHORT).show()
        }
    }
    
    // 验证并打开相册
    private fun verifyAndOpenGallery() {
        BiometricInitializer.authenticateBeforeMethod(
            requireActivity(),
            "openGallery",
            onSuccess = { openGalleryForImageSelection() },
            onError = { errorCode, errorMessage ->
                Log.e(TAG, "生物识别验证失败: $errorCode, $errorMessage")
                Toast.makeText(requireContext(), "验证失败: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    // 打开相册选择图片
    fun openGalleryForImageSelection() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryPickerResult.launch(intent)
    }
    
    // 验证并分析图片
    private fun verifyAndAnalyzeImage(imageUri: Uri) {
        BiometricInitializer.authenticateBeforeMethod(
            requireActivity(),
            "analyzeImage",
            onSuccess = { analyzeImage(imageUri) },
            onError = { errorCode, errorMessage ->
                Log.e(TAG, "生物识别验证失败: $errorCode, $errorMessage")
                Toast.makeText(requireContext(), "验证失败: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    // 分析选中的图片
    fun analyzeImage(imageUri: Uri) {
        try {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            intent.putExtra("IMAGE_URI", imageUri)
            cameraActivityResult.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "启动图像分析失败: ${e.message}")
            Toast.makeText(requireContext(), "无法进行图像分析", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDetach() {
        super.onDetach()
        biometricCallback = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        // 清理资源
    }
} 