import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.util.concurrent.TimeUnit
import android.graphics.ImageFormat
import android.media.ImageReader
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.content.Context

class CameraHandler(private val context: Context, private val cameraManager: CameraManager) {

    private lateinit var imageReader: ImageReader

    fun startCameraCapture(onImageAvailable: (Bitmap) -> Unit) {
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
//            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            // 设置 ImageReader 以获取图像
            imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 1)
            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                // 将 Image 转换为 Bitmap
                val bitmap = image.toBitmap()
                onImageAvailable(bitmap)
                image.close()
            }, null)

            val cameraDeviceCallback = object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    // 设置捕捉逻辑
                    Log.d("CameraHandler", "Camera opened successfully.")
                    // 启动捕捉请求
                    // 这里需要实现捕捉请求的逻辑
                }
                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }
                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e("CameraHandler", "Camera error: $error")
                }
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            cameraManager.openCamera(cameraId, cameraDeviceCallback, null)
        } catch (e: Exception) {
            Log.e("CameraHandler", "Error starting camera: ", e)
        }
    }

    private fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // 使用 BitmapFactory 将字节数组转换为 Bitmap
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
