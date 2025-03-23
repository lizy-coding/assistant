package com.example.assistant

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.image_analysis.ImageAnalysisDemoFragment

class ImageAnalysisActivity : AppCompatActivity() {
    
    private val TAG = "ImageAnalysisActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_analysis)
        
        Log.d(TAG, "创建图像分析Activity")
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ImageAnalysisDemoFragment())
                .commit()
            
            Log.d(TAG, "加载图像分析Fragment")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "销毁图像分析Activity")
    }
} 