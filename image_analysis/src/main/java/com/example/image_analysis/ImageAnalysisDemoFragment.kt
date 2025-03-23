package com.example.image_analysis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ImageAnalysisDemoFragment : Fragment() {

    private val TAG = "ImageAnalysisDemoFragment"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        // 修改为使用 image_analysis 模块的布局
        return inflater.inflate(R.layout.fragment_image_analysis_demo, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        
        // 在这里初始化图像分析相关的功能
        initImageAnalysis()
    }
    
    private fun initImageAnalysis() {
        // 实现图像分析的初始化逻辑
        // 可以从原 ImageAnalysisActivity 中迁移核心功能
        Log.d(TAG, "初始化图像分析")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        // 清理资源
    }
} 