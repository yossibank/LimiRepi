package jp.co.yahoo.yossibank.limirepi.feature.receipt.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import jp.co.yahoo.yossibank.limirepi.feature.receipt.model.ReceiptData
import jp.co.yahoo.yossibank.limirepi.feature.receipt.ocr.ReceiptOcrService
import jp.co.yahoo.yossibank.limirepi.util.logger.AppLogger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readValue
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetPhoto
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVVideoCodecKey
import platform.AVFoundation.AVVideoCodecTypeJPEG
import platform.AVFoundation.fileDataRepresentation
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSError
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

@Composable
actual fun CameraScreen(
    modifier: Modifier,
    captureTrigger: Boolean,
    onCaptureFinished: () -> Unit,
    onAnalyzing: (Boolean) -> Unit,
    onParsed: (ReceiptData?) -> Unit
) {
    val session = remember { AVCaptureSession() }
    val photoOutput = remember { AVCapturePhotoOutput() }
    val ocrService = remember { ReceiptOcrService() }

    // DisposableEffectでリソースのクリーンアップ
    DisposableEffect(Unit) {
        onDispose {
            ocrService.close()
        }
    }

    UIKitViewController(
        factory = {
            val controller = UIViewCapturer(
                session = session,
                photoOutput = photoOutput,
                onImageCaptured = { imageData ->
                    // 画像キャプチャ完了を通知（この時点でカメラ画面を閉じる）
                    dispatch_async(dispatch_get_main_queue()) {
                        onCaptureFinished()
                        onAnalyzing(true)
                    }

                    // 解析処理（バックグラウンドで実行）
                    MainScope().launch {
                        try {
                            val receiptData = ocrService.scanReceipt(imageData)
                            dispatch_async(dispatch_get_main_queue()) {
                                onAnalyzing(false)
                                onParsed(receiptData)
                            }
                        } catch (e: Exception) {
                            AppLogger.e("CameraScreen", "Error scanning receipt: ${e.message}")
                            dispatch_async(dispatch_get_main_queue()) {
                                onAnalyzing(false)
                                onParsed(null)
                            }
                        }
                    }
                }
            )
            setupCaptureSession(session, photoOutput)
            controller
        },
        modifier = modifier,
        update = { controller ->
            if (captureTrigger) {
                controller.capturePhoto()
            }
        },
        onRelease = {
            session.stopRunning()
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun setupCaptureSession(
    session: AVCaptureSession,
    photoOutput: AVCapturePhotoOutput
) {
    val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return
    val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null) ?: return

    session.beginConfiguration()
    if (session.canAddInput(input)) session.addInput(input)
    if (session.canAddOutput(photoOutput)) session.addOutput(photoOutput)
    session.sessionPreset = AVCaptureSessionPresetPhoto
    session.commitConfiguration()

    dispatch_async(dispatch_get_main_queue()) {
        if (!session.isRunning()) {
            session.startRunning()
        }
    }
}

class UIViewCapturer(
    private val session: AVCaptureSession,
    private val photoOutput: AVCapturePhotoOutput,
    private val onImageCaptured: (ByteArray) -> Unit
) : UIViewController(null, null), AVCapturePhotoCaptureDelegateProtocol {

    private val previewLayer = AVCaptureVideoPreviewLayer(session = session).apply {
        videoGravity = AVLayerVideoGravityResizeAspectFill
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun loadView() {
        view = UIView(frame = CGRectZero.readValue())
        view.layer.addSublayer(previewLayer)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        previewLayer.frame = view.bounds
    }

    fun capturePhoto() {
        val settings = AVCapturePhotoSettings.photoSettingsWithFormat(
            mapOf(AVVideoCodecKey to AVVideoCodecTypeJPEG)
        )
        photoOutput.capturePhotoWithSettings(settings, delegate = this)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: NSError?
    ) {
        if (error != null) {
            AppLogger.e("CameraScreen", "Capture error: ${error.localizedDescription}")
            return
        }

        val imageData = didFinishProcessingPhoto.fileDataRepresentation() ?: run {
            AppLogger.e("CameraScreen", "Failed to get image data")
            return
        }

        // NSDataをByteArrayに変換
        val byteArray = ByteArray(imageData.length.toInt())
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), imageData.bytes, imageData.length)
        }

        // 画像データをコールバックに渡す
        onImageCaptured(byteArray)
    }
}
