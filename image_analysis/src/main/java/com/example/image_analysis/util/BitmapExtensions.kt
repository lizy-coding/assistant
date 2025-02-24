import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

// 自动旋转扩展函数
fun Bitmap.autoRotate(): Bitmap {
    val exif = try {
        val stream = ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG, 90, this)
        }
        ExifInterface(ByteArrayInputStream(stream.toByteArray()))
    } catch (e: Exception) {
        return this
    }

    return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(270f)
        else -> this
    }
}

private fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

// 银行卡区域裁剪（基础实现）
fun Bitmap.cropCardArea(): Bitmap {
    // 实际项目应使用OpenCV进行区域检测，此处演示固定裁剪
    val cardRatio = 85.6f / 53.98f // 标准银行卡比例
    val targetHeight = (width / cardRatio).toInt()
    val cropHeight = if (height > targetHeight) targetHeight else height

    return Bitmap.createBitmap(
        this,
        0,
        (height - cropHeight) / 2,
        width,
        cropHeight
    )
}

// 图像二值化处理
fun Bitmap.binarize(threshold: Int = 128): Bitmap {
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val red = (pixels[i] shr 16) and 0xFF
        val green = (pixels[i] shr 8) and 0xFF
        val blue = pixels[i] and 0xFF
        val gray = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
        val newPixel = if (gray > threshold) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
        pixels[i] = newPixel
    }

    output.setPixels(pixels, 0, width, 0, 0, width, height)
    return output
}