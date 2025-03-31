package com.example.assistant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.image_analysis.BiometricInitializer
import com.example.image_analysis.CameraActivity
import com.example.image_analysis.ImageAnalysisDemoFragment

class ImageAnalysisActivity : AppCompatActivity() {
    
    private val TAG = "ImageAnalysisActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_analysis)
        
        Log.d(TAG, "创建图像分析Activity")
        
        // 初始化生物识别功能
        BiometricInitializer.initialize(this)
        
        if (savedInstanceState == null) {
            // 在加载Fragment前进行生物识别验证
            BiometricInitializer.authenticateBeforeMethod(
                this,
                "loadFragment",
                onSuccess = {
                    // 验证成功后加载Fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ImageAnalysisDemoFragment())
                        .commit()
                    
                    Log.d(TAG, "加载图像分析Fragment")
                },
                onError = { errorCode, errorMessage ->
                    Log.e(TAG, "生物识别验证失败: $errorCode, $errorMessage")
                    Toast.makeText(this, "验证失败: $errorMessage", Toast.LENGTH_SHORT).show()
                    finish() // 验证失败则关闭Activity
                }
            )
        }
    }
    
    /**
     * 启动相机Activity并进行生物识别验证
     */
    fun startCameraWithBiometric() {
        BiometricInitializer.authenticateBeforeMethod(
            this,
            "startCamera",
            onSuccess = {
                // 验证成功后启动相机
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            },
            onError = { errorCode, errorMessage ->
                Log.e(TAG, "生物识别验证失败: $errorCode, $errorMessage")
                Toast.makeText(this, "验证失败: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "销毁图像分析Activity")
    }
} 