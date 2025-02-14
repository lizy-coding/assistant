
package com.example.assistant
import CameraHandler
import ImageParser
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.assistant.R
import kotlinx.coroutines.launch
import android.hardware.camera2.CameraManager
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

// MainActivity.kt in app module

class MainActivity : AppCompatActivity() {

    private lateinit var imageParser: ImageParser
    private lateinit var cameraHandler: CameraHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 ImageParser
        imageParser = ImageParser()
        
        // 初始化 CameraHandler
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraHandler = CameraHandler(this, cameraManager) // 传递 context 参数

        val captureButton: Button = findViewById(R.id.captureButton)
        captureButton.setOnClickListener {
            // 检查相机权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // 请求相机权限
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                // 启动相机捕捉并处理 Bitmap
                cameraHandler.startCameraCapture { bitmap ->
                    lifecycleScope.launch {
                        val result = imageParser.parseImage(bitmap)
                        // 处理解析结果
                        println("解析结果: $result")
                    }
                }
            }
        }
    }

    // 处理权限请求结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 权限被授予，启动相机捕捉
                cameraHandler.startCameraCapture { bitmap ->
                    lifecycleScope.launch {
                        val result = imageParser.parseImage(bitmap)
                        // 处理解析结果
                        println("解析结果: $result")
                    }
                }
            } else {
                // 权限被拒绝，显示提示
                Toast.makeText(this, "相机权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100 // 请求码
    }
}
