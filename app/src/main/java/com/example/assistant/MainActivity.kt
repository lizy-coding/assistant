package com.example.assistant
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.image_analysis.CameraActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        System.setProperty("file.encoding", "UTF-8")
        val enterCameraButton: Button = findViewById(R.id.enterCameraButton)
        val exitButton: Button = findViewById(R.id.exitButton)

        enterCameraButton.setOnClickListener {
            requestPermissions()
            // 检查相机和存储权限
            Log.e("MainActivity", "Permissions=${checkPermissions()}")

            if (checkPermissions()) {
                // 进入拍照界面
                startActivity(Intent(this, CameraActivity::class.java))
            } else {
                Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show()
                showPermissionDenied()
            }
        }

        exitButton.setOnClickListener {
            finish() // 退出应用
        }
    }

    private fun checkPermissions(): Boolean {
        return  checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//        return (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
//                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
    }

    private fun showPermissionDenied() {
        AlertDialog.Builder(this)
            .setTitle("权限被拒绝")
            .setMessage("请在设置中开启相机权限")
            .setPositiveButton("去设置") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .show()
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 100 // 请求码
        private val REQUEST_CODE_PERMISSIONS = 1001
    }
}