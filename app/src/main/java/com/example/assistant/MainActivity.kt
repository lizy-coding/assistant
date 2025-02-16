package com.example.assistant
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.image_analysis.CameraActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val enterCameraButton: Button = findViewById(R.id.enterCameraButton)
        val exitButton: Button = findViewById(R.id.exitButton)

        enterCameraButton.setOnClickListener {
            requestPermissions()
            // 检查相机和存储权限
            if (checkPermissions()) {
                // 进入拍照界面
                startActivity(Intent(this, CameraActivity::class.java))
            } else {
                requestPermissions()
            }
        }

        exitButton.setOnClickListener {
            finish() // 退出应用
        }
    }

    private fun checkPermissions(): Boolean {
        return (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 100 // 请求码
    }
}