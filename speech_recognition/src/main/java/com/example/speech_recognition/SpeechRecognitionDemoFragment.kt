package com.example.speech_recognition

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * 语音识别演示Fragment
 */
class SpeechRecognitionDemoFragment : Fragment(), SpeechRecognitionCallback {
    
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var statusTextView: TextView
    
    private lateinit var baiduSpeechRecognizer: BaiduSpeechRecognizer
    
    private val TAG = "SpeechRecognitionDemo"
    private val RECORD_AUDIO_PERMISSION_CODE = 101
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_speech_recognition_demo, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        startButton = view.findViewById(R.id.start_button)
        stopButton = view.findViewById(R.id.stop_button)
        resultTextView = view.findViewById(R.id.result_text_view)
        statusTextView = view.findViewById(R.id.status_text_view)
        
        // 初始化语音识别模块
        context?.let {
            baiduSpeechRecognizer = BaiduSpeechRecognizer(it)
            baiduSpeechRecognizer.setCallback(this)
            baiduSpeechRecognizer.initialize(
                "test_app_id",  // 仅用于测试
                "test_api_key", // 仅用于测试
                "test_secret_key" // 仅用于测试
            )
            
            Log.i(TAG, "语音识别初始化完成")
        }
        
        startButton.setOnClickListener {
            Log.d(TAG, "点击开始按钮")
            if (checkPermission()) {
                baiduSpeechRecognizer.startRecognition()
            } else {
                requestPermission()
            }
        }
        
        stopButton.setOnClickListener {
            Log.d(TAG, "点击停止按钮")
            baiduSpeechRecognizer.stopRecognition()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        baiduSpeechRecognizer.release()
        Log.i(TAG, "释放语音识别资源")
    }
    
    override fun onRecognitionStart() {
        Log.d(TAG, "识别开始")
        activity?.runOnUiThread {
            statusTextView.text = "正在听取..."
            startButton.isEnabled = false
            stopButton.isEnabled = true
        }
    }
    
    @SuppressLint("SetTextI18n")
    override fun onPartialResult(result: String) {
        Log.d(TAG, "部分识别结果: $result")
        activity?.runOnUiThread {
            resultTextView.text = "临时结果: $result"
        }
    }
    
    @SuppressLint("SetTextI18n")
    override fun onResult(result: String) {
        Log.d(TAG, "最终识别结果: $result")
        activity?.runOnUiThread {
            resultTextView.text = "最终结果: $result"
        }
    }
    
    override fun onError(errorCode: Int, errorMessage: String) {
        Log.e(TAG, "识别错误: $errorCode - $errorMessage")
        activity?.runOnUiThread {
            statusTextView.text = "错误: $errorMessage (代码: $errorCode)"
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }
    
    override fun onRecognitionEnd() {
        Log.d(TAG, "识别结束")
        activity?.runOnUiThread {
            statusTextView.text = "识别结束"
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }
    
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baiduSpeechRecognizer.startRecognition()
            } else {
                Toast.makeText(context, "需要录音权限才能使用语音识别功能", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 