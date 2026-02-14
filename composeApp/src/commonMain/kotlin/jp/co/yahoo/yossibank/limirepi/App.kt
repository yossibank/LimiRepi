package jp.co.yahoo.yossibank.limirepi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
fun App() {
    var showCamera by remember { mutableStateOf(false) }
    var receiptData by remember { mutableStateOf<ReceiptData?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
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
                                // 画像キャプチャ完了後にカメラ画面を閉じる
                                showCamera = false
                            },
                            onAnalyzing = { analyzing ->
                                isAnalyzing = analyzing
                            },
                            onParsed = { data ->
                                isAnalyzing = false
                                if (data != null) {
                                    receiptData = data
                                    showSheet = true
                                }
                            }
                        )

                        // UIボタン
                        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)) {
                            Button(
                                onClick = { isCapturing = true },
                                modifier = Modifier.size(72.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                enabled = !isAnalyzing
                            ) {
                                Icon(Icons.Default.PhotoCamera, "撮影", tint = Color.Black)
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
                // ホーム画面
                else -> {
                    Column(
                        Modifier.fillMaxSize(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Button(onClick = { showCamera = true }) { Text("📷 レシートをスキャン") }
                    }
                }
            }

            if (showSheet) {
                ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                    receiptData?.let { data ->
                        ReceiptResultSheet(data) { showSheet = false }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AnalyzingScreen(modifier: Modifier = Modifier) {
    // Lottieアニメーションの読み込み
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/loading_animation.json").decodeToString()
        )
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE // 無限ループ
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
            // Lottieアニメーション
            androidx.compose.foundation.Image(
                painter = painter,
                contentDescription = "Loading animation",
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "AI解析中...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "レシート情報を読み取っています",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ReceiptResultSheet(data: ReceiptData, onClose: () -> Unit) {
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
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(item.name)
                        Row {
                            if (item.quantity > 1) {
                                Text(
                                    "数量: ${item.quantity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            item.category?.let { category ->
                                Text(
                                    "[$category]",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    Text(
                        text = "¥${item.price}",
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(Modifier.alpha(0.3f))
            }
        }

        // 合計金額
        data.totalAmount?.let { total ->
            HorizontalDivider()
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

        Button(onClick = onClose, Modifier.fillMaxWidth()) { Text("閉じる") }
    }
}