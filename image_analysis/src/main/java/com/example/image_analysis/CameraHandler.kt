import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
//import com.example.image_analysis.server.AliyunOcrHelper
import java.io.File

class CameraHandler(private val context: Context) {

    private lateinit var imageCapture: ImageCapture
    private lateinit var preview: Preview

    fun startCameraCapture(previewView: PreviewView, storageDir: File, onImageAvailable: (Bitmap) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 设置图像捕捉
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // 设置预览
            preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider // 将预览与视图绑定
            }

            // 选择后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 绑定生命周期
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("CameraHandler", "Use case binding failed", exc)
            }

            // 拍照并保存到指定目录
            val outputFile = File(storageDir, "photo_${System.currentTimeMillis()}.jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // 处理保存的图像
                    val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                    Log.e("CameraHandler", "outputFile.absolutePath=${outputFile.absolutePath}")
                    onImageAvailable(bitmap)

                    // 通知媒体库更新
                    MediaScannerConnection.scanFile(context, arrayOf(outputFile.absolutePath), null, null)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraHandler", "Error capturing image: ", exception)
                }
            })
        }, ContextCompat.getMainExecutor(context))
    }
}
