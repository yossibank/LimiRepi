package jp.co.yahoo.yossibank.limirepi.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.yahoo.yossibank.limirepi.ocr.ReceiptData

@Composable
expect fun CameraScreen(
    modifier: Modifier,
    captureTrigger: Boolean,
    onCaptureFinished: () -> Unit,
    onAnalyzing: (Boolean) -> Unit,
    onParsed: (ReceiptData?) -> Unit
)