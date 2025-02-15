import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File

class CameraHandler(private val context: Context) {

    private lateinit var imageCapture: ImageCapture

    fun startCameraCapture(onImageAvailable: (Bitmap) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 设置图像捕捉
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // 选择后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 绑定生命周期
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner, cameraSelector, imageCapture
                )

                // 创建输出文件
                val outputFile = File(context.externalMediaDirs.first(), "photo.jpg")
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                // 拍照
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // 处理保存的图像
                        val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                        onImageAvailable(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraHandler", "Error capturing image: ", exception)
                    }
                })

            } catch (exc: Exception) {
                Log.e("CameraHandler", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    }
