import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.assistant.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cameraHandler: CameraHandler
    private lateinit var imageParser: ImageParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraHandler = CameraHandler(getSystemService(CAMERA_SERVICE) as CameraManager)
        imageParser = ImageParser()

        val captureButton: Button = findViewById(R.id.captureButton)
        captureButton.setOnClickListener {
            // 假设你已经拍照并得到了 Bitmap 对象
//            val bitmap = /* 从相机获取的 Bitmap */
//                lifecycleScope.launch {
//                    val result = imageParser.parseImage(bitmap)
//                    // 处理解析结果
//                    println("解析结果: $result")
//                }
        }
    }
}
