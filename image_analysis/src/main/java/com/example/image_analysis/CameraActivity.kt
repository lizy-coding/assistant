package com.example.image_analysis
import CameraHandler
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraHandler: CameraHandler
    private lateinit var previewView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

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

    private fun startCameraCapture() {
        // 获取存储路径（相册目录）
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp")

        // 创建目录（如果不存在）
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        cameraHandler.startCameraCapture(previewView, storageDir) { bitmap ->
            // 处理返回的 Bitmap
            lifecycleScope.launch {
                // 这里可以处理 Bitmap，例如显示在 ImageView 中
                Toast.makeText(this@CameraActivity, "Image captured and saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}