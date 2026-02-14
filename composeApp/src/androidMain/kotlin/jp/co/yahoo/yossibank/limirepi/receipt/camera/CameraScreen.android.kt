package jp.co.yahoo.yossibank.limirepi.receipt.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import jp.co.yahoo.yossibank.limirepi.receipt.model.ReceiptData
import jp.co.yahoo.yossibank.limirepi.receipt.ocr.ReceiptOcrService
import jp.co.yahoo.yossibank.limirepi.logger.AppLogger
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Composable
actual fun CameraScreen(
    modifier: Modifier,
    captureTrigger: Boolean,
    onCaptureFinished: () -> Unit,
    onAnalyzing: (Boolean) -> Unit,
    onParsed: (ReceiptData?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val ocrService = remember { ReceiptOcrService() }

    // DisposableEffectでリソースのクリーンアップ
    DisposableEffect(Unit) {
        onDispose {
            ocrService.close()
        }
    }

    LaunchedEffect(captureTrigger) {
        if (captureTrigger) {
            val mainExecutor = ContextCompat.getMainExecutor(context)
            
            imageCapture.takePicture(
                mainExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    @SuppressLint("UnsafeOptInUsageError")
                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                        // 画像キャプチャ完了を通知（この時点でカメラ画面を閉じる）
                        onCaptureFinished()
                        onAnalyzing(true)

                        // 解析処理（バックグラウンドで実行）
                        kotlinx.coroutines.MainScope().launch {
                            try {
                                // ImageProxyをJPEG ByteArrayに変換
                                val jpegBytes = imageProxyToJpegByteArray(imageProxy)

                                // Gemini APIで解析
                                val receiptData = ocrService.scanReceiptWithAI(jpegBytes)

                                onAnalyzing(false)
                                onParsed(receiptData)
                            } catch (e: Exception) {
                                AppLogger.e("CameraScreen", "Error analyzing receipt: ${e.message}")
                                onAnalyzing(false)
                                onParsed(null)
                            } finally {
                                imageProxy.close()
                            }
                        }
                    }

                    override fun onError(exc: ImageCaptureException) {
                        AppLogger.e("CameraScreen", "Image capture error: ${exc.message}")
                        onCaptureFinished()
                        onParsed(null)
                    }
                })
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        update = { previewView ->
            val providerFuture = ProcessCameraProvider.getInstance(context)
            providerFuture.addListener({
                val provider = providerFuture.get()
                val preview = Preview.Builder().build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

/**
 * ImageProxyをJPEG形式のByteArrayに変換する
 * Critical Issue修正: YUV形式からJPEGへの正しい変換
 * パフォーマンス最適化: 画像を縮小してAPI送信サイズを削減
 */
@SuppressLint("UnsafeOptInUsageError")
private fun imageProxyToJpegByteArray(imageProxy: ImageProxy): ByteArray {
    val image = imageProxy.image ?: throw IllegalStateException("Image is null")
    
    return when (imageProxy.format) {
        ImageFormat.JPEG -> {
            // JPEG形式の場合はそのまま使用
            val buffer = imageProxy.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            
            // Bitmapにデコードして縮小
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val resizedBitmap = resizeBitmapForOCR(bitmap)
            
            val out = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            resizedBitmap.recycle()
            bitmap.recycle()
            
            out.toByteArray()
        }
        ImageFormat.YUV_420_888 -> {
            // YUV形式の場合はJPEGに変換
            val yBuffer = imageProxy.planes[0].buffer
            val uBuffer = imageProxy.planes[1].buffer
            val vBuffer = imageProxy.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 85, out)
            
            // 回転補正
            val bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
            val rotatedBitmap = rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees.toFloat())
            
            // OCR用に画像を縮小
            val resizedBitmap = resizeBitmapForOCR(rotatedBitmap)
            
            val finalOut = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, finalOut)
            resizedBitmap.recycle()
            rotatedBitmap.recycle()
            bitmap.recycle()
            
            finalOut.toByteArray()
        }
        else -> {
            throw IllegalArgumentException("Unsupported image format: ${imageProxy.format}")
        }
    }
}

/**
 * OCR用に画像を適切なサイズに縮小
 * レシート解析には1920x1080程度で十分
 */
private fun resizeBitmapForOCR(bitmap: Bitmap): Bitmap {
    val maxWidth = 1920
    val maxHeight = 1920
    
    val width = bitmap.width
    val height = bitmap.height
    
    // すでに十分小さい場合はそのまま返す
    if (width <= maxWidth && height <= maxHeight) {
        return bitmap
    }
    
    // アスペクト比を維持しながら縮小
    val scale = minOf(
        maxWidth.toFloat() / width,
        maxHeight.toFloat() / height
    )
    
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

/**
 * Bitmapを指定角度回転させる
 */
private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    if (degrees == 0f) return bitmap
    
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
