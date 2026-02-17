package jp.co.yahoo.yossibank.limirepi.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import jp.co.yahoo.yossibank.limirepi.receipt.camera.CameraScreen
import jp.co.yahoo.yossibank.limirepi.receipt.model.ReceiptData
import limirepi.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

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
                CameraView(
                    modifier = Modifier.fillMaxSize(),
                    isCapturing = isCapturing,
                    isAnalyzing = isAnalyzing,
                    onCaptureClick = { isCapturing = true },
                    onCancelClick = { showCamera = false },
                    onCaptureFinished = {
                        isCapturing = false
                        showCamera = false
                    },
                    onAnalyzing = { isAnalyzing = it },
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
            }
            // 通常の登録画面
            else -> {
                InitialRegisterView(
                    modifier = Modifier.fillMaxSize(),
                    onCameraClick = {
                        analysisErrorMessage = null
                        showCamera = true
                    }
                )
            }
        }

        // 解析結果のBottomSheet
        if (showSheet && receiptData != null) {
            ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                ReceiptResultSheet(receiptData!!, onClose = { showSheet = false })
            }
        }

        // エラーメッセージのBottomSheet
        if (analysisErrorMessage != null) {
            ModalBottomSheet(onDismissRequest = { analysisErrorMessage = null }) {
                ReceiptLoadErrorSheet(
                    message = analysisErrorMessage!!,
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

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun AnalyzingScreen(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/loading_animation.json").decodeToString()
        )
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE
    )

    val painter = rememberLottiePainter(
        composition = composition,
        progress = { progress }
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "Loading animation",
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "AI解析中...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "レシート情報を読み取っています",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InitialRegisterView(
    modifier: Modifier = Modifier,
    onCameraClick: () -> Unit
) {
    Column(
        modifier = modifier,
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = onCameraClick) {
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

@Composable
private fun CameraView(
    modifier: Modifier = Modifier,
    isCapturing: Boolean,
    isAnalyzing: Boolean,
    onCaptureClick: () -> Unit,
    onCancelClick: () -> Unit,
    onCaptureFinished: () -> Unit,
    onAnalyzing: (Boolean) -> Unit,
    onParsed: (ReceiptData?) -> Unit
) {
    Box(modifier = modifier) {
        CameraScreen(
            modifier = Modifier.fillMaxSize(),
            captureTrigger = isCapturing,
            onCaptureFinished = onCaptureFinished,
            onAnalyzing = onAnalyzing,
            onParsed = onParsed
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCaptureClick,
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                enabled = !isAnalyzing
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "撮影",
                    modifier = Modifier.size(32.dp)
                )
            }

            TextButton(
                onClick = onCancelClick,
                enabled = !isAnalyzing
            ) {
                Text(
                    text = "キャンセル",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ReceiptResultSheet(data: ReceiptData, onClose: () -> Unit) {
    val fallbackSubtotal = data.items.sumOf { it.priceAfterDiscount }
    val subtotalAmountToShow =
        data.subtotalAmount ?: fallbackSubtotal.takeIf { data.items.isNotEmpty() }
    val taxBreakdownsToShow = data.taxBreakdowns
    val taxAmountToShow = data.taxAmount ?: taxBreakdownsToShow.sumOf { it.amount }
        .takeIf { taxBreakdownsToShow.isNotEmpty() }
    val totalAmountToShow = data.totalAmount
        ?: if (subtotalAmountToShow != null && taxAmountToShow != null) {
            subtotalAmountToShow + taxAmountToShow
        } else {
            null
        }

    Column(Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 700.dp)) {
        Text("スキャン結果", style = MaterialTheme.typography.titleLarge)

        // レシート情報
        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            data.storeName?.let {
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("店舗名: ", fontWeight = FontWeight.Bold)
                    Text(it)
                }
            }
            data.purchaseDate?.let {
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("購入日: ", fontWeight = FontWeight.Bold)
                    Text(it)
                }
            }
        }

        HorizontalDivider()

        // 商品リスト
        LazyColumn(Modifier.weight(1f).padding(vertical = 16.dp)) {
            items(data.items) { item ->
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    // 商品名と価格
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Medium)
                            Row {
                                if (item.quantity > 1) {
                                    Text(
                                        "数量: ${item.quantity}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                Text(
                                    "[${item.category}]",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black
                                )
                            }
                        }
                        Text(
                            text = "¥${item.price}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 割引情報
                    item.discount?.let { discount ->
                        Row(
                            Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp),
                            Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = discount.name + (discount.percentage?.let { " ($it%)" }
                                    ?: ""),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Red
                            )
                            Text(
                                text = "-¥${item.discountAmount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                HorizontalDivider(Modifier.alpha(0.3f))
            }
        }

        // 小計・消費税内訳
        if (subtotalAmountToShow != null || taxBreakdownsToShow.isNotEmpty() || taxAmountToShow != null) {
            HorizontalDivider()
            subtotalAmountToShow?.let { subtotal ->
                Row(
                    Modifier.fillMaxWidth().padding(top = 12.dp),
                    Arrangement.SpaceBetween
                ) {
                    Text("小計", fontWeight = FontWeight.Medium)
                    Text("¥$subtotal", fontWeight = FontWeight.Medium)
                }
            }

            if (taxBreakdownsToShow.isNotEmpty()) {
                taxBreakdownsToShow.forEach { tax ->
                    Row(
                        Modifier.fillMaxWidth().padding(top = 6.dp),
                        Arrangement.SpaceBetween
                    ) {
                        Text(
                            tax.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            "¥${tax.amount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
            } else {
                taxAmountToShow?.let { taxAmount ->
                    Row(
                        Modifier.fillMaxWidth().padding(top = 6.dp),
                        Arrangement.SpaceBetween
                    ) {
                        Text(
                            "消費税",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            "¥$taxAmount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // 合計金額
        totalAmountToShow?.let { total ->
            HorizontalDivider(Modifier.padding(top = 12.dp))
            Row(
                Modifier.fillMaxWidth().padding(vertical = 16.dp),
                Arrangement.SpaceBetween
            ) {
                Text(
                    "合計金額",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "¥$total",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Button(onClick = onClose, Modifier.fillMaxWidth()) {
            Text("閉じる")
        }
    }
}

@Composable
private fun ReceiptLoadErrorSheet(
    message: String,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "⚠️ 読み込みエラー",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Text("再撮影する")
            }

            TextButton(
                onClick = onClose,
                modifier = Modifier.weight(1f)
            ) {
                Text("閉じる")
            }
        }
    }
}

