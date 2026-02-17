package jp.co.yahoo.yossibank.limirepi.feature.receipt.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.yahoo.yossibank.limirepi.feature.receipt.model.ReceiptData

@Composable
expect fun CameraScreen(
    modifier: Modifier = Modifier,
    captureTrigger: Boolean,
    onCaptureFinished: () -> Unit,
    onAnalyzing: (Boolean) -> Unit,
    onParsed: (ReceiptData?) -> Unit
)
