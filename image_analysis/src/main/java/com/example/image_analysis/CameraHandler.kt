import android.hardware.camera2.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.concurrent.TimeUnit

class CameraHandler(private val cameraManager: CameraManager) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startCameraCapture() {
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val cameraDeviceCallback = object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    // 设置捕捉逻辑
                    Log.d("CameraHandler", "Camera opened successfully.")
                }
                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }
                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e("CameraHandler", "Camera error: $error")
                }
            }
            cameraManager.openCamera(cameraId, cameraDeviceCallback, null)
        } catch (e: Exception) {
            Log.e("CameraHandler", "Error starting camera: ", e)
        }
    }
}
