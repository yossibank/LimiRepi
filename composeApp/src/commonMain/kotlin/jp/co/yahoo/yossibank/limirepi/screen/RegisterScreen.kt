package jp.co.yahoo.yossibank.limirepi.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.co.yahoo.yossibank.limirepi.AnalyzingScreen
import jp.co.yahoo.yossibank.limirepi.ReceiptLoadErrorSheet
import jp.co.yahoo.yossibank.limirepi.ReceiptResultSheet
import jp.co.yahoo.yossibank.limirepi.receipt.camera.CameraScreen
import jp.co.yahoo.yossibank.limirepi.receipt.model.ReceiptData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    var showCamera by remember { mutableStateOf(false) }
    var receiptData by remember { mutableStateOf<ReceiptData?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisErrorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            // AI解析画面
            isAnalyzing -> {
                AnalyzingScreen()
            }
            // カメラ画面
            showCamera -> {
                Box(Modifier.fillMaxSize()) {
                    CameraScreen(
                        modifier = Modifier.fillMaxSize(),
                        captureTrigger = isCapturing,
                        onCaptureFinished = {
                            isCapturing = false
                            showCamera = false
                        },
                        onAnalyzing = { analyzing ->
                            isAnalyzing = analyzing
                        },
                        onParsed = { data ->
                            isAnalyzing = false

                            if (data != null) {
                                receiptData = data
                                analysisErrorMessage = null
                                showSheet = true
                            } else {
                                receiptData = null
                                showSheet = false
                                analysisErrorMessage =
                                    "レシートの読み込みに失敗しました。画像がぶれていないか確認して、もう一度お試しください。"
                            }
                        }
                    )

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp)
                    ) {
                        Button(
                            onClick = { isCapturing = true },
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            enabled = !isAnalyzing
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "撮影",
                                tint = Color.Black
                            )
                        }
                        TextButton(
                            onClick = { showCamera = false },
                            enabled = !isAnalyzing
                        ) {
                            Text("キャンセル", color = Color.White)
                        }
                    }
                }
            }
            // 通常の登録画面
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "食材登録",
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF2196F3)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "食材登録",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "レシートから食材を登録できます",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            analysisErrorMessage = null
                            showCamera = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text("📷 レシートをスキャン")
                    }
                }
            }
        }

        // ModalBottomSheet
        if (showSheet) {
            ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                receiptData?.let { data ->
                    ReceiptResultSheet(data) { showSheet = false }
                }
            }
        }

        analysisErrorMessage?.let { message ->
            ModalBottomSheet(onDismissRequest = { analysisErrorMessage = null }) {
                ReceiptLoadErrorSheet(
                    message = message,
                    onRetry = {
                        analysisErrorMessage = null
                        showCamera = true
                    },
                    onClose = { analysisErrorMessage = null }
                )
            }
        }
    }
}
