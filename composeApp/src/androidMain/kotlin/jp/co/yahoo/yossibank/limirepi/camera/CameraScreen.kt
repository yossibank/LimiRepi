package jp.co.yahoo.yossibank.limirepi.camera

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import jp.co.yahoo.yossibank.limirepi.AppLogger
import jp.co.yahoo.yossibank.limirepi.ocr.ReceiptData
import jp.co.yahoo.yossibank.limirepi.ocr.ReceiptOcrService
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.Executors

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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(captureTrigger) {
        if (captureTrigger) {
            imageCapture.takePicture(
                Executors.newSingleThreadExecutor(),
                object : ImageCapture.OnImageCapturedCallback() {
                    @SuppressLint("UnsafeOptInUsageError")
                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                        onAnalyzing(true)
                        
                        coroutineScope.launch {
                            try {
                                // ImageProxyをByteArrayに変換
                                val buffer: ByteBuffer = imageProxy.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining())
                                buffer.get(bytes)
                                
                                // Gemini APIで解析
                                val receiptData = ocrService.scanReceiptWithAI(bytes)
                                
                                onAnalyzing(false)
                                onParsed(receiptData)
                            } catch (e: Exception) {
                                AppLogger.e("CameraScreen", "Error analyzing receipt: ${e.message}")
                                onAnalyzing(false)
                                onParsed(null)
                            } finally {
                                imageProxy.close()
                                onCaptureFinished()
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