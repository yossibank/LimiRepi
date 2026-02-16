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
import jp.co.yahoo.yossibank.limirepi.navigation.PlatformTabScaffold
import jp.co.yahoo.yossibank.limirepi.navigation.TabAction
import jp.co.yahoo.yossibank.limirepi.navigation.TabItem
import jp.co.yahoo.yossibank.limirepi.receipt.camera.CameraScreen
import jp.co.yahoo.yossibank.limirepi.receipt.model.ReceiptData
import jp.co.yahoo.yossibank.limirepi.screen.FridgeScreen
import jp.co.yahoo.yossibank.limirepi.screen.RecipeGenerateScreen
import jp.co.yahoo.yossibank.limirepi.screen.RecipeListScreen
import jp.co.yahoo.yossibank.limirepi.screen.RegisterScreen
import jp.co.yahoo.yossibank.limirepi.screen.SettingsScreen
import limirepi.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var selectedTab by remember { mutableStateOf(TabItem.FRIDGE) }

    MaterialTheme {
        PlatformTabScaffold(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        ) { tab ->
            TabContent(tab = tab)
        }
    }
}

@Composable
fun TabContent(
    tab: TabItem,
    modifier: Modifier = Modifier
) {
    when (tab) {
        TabItem.FRIDGE -> FridgeScreen(modifier)
        TabItem.REGISTER -> RegisterScreen(modifier)
        TabItem.RECIPE_GENERATE -> RecipeGenerateScreen(modifier)
        TabItem.RECIPE_LIST -> RecipeListScreen(modifier)
        TabItem.SETTINGS -> SettingsScreen(modifier)
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
    val fallbackSubtotal = data.items.sumOf { it.priceAfterDiscount }
    val subtotalAmountToShow = data.subtotalAmount ?: fallbackSubtotal.takeIf { data.items.isNotEmpty() }
    val taxBreakdownsToShow = data.taxBreakdowns
    val taxAmountToShow = data.taxAmount ?: taxBreakdownsToShow.sumOf { it.amount }.takeIf { taxBreakdownsToShow.isNotEmpty() }
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
                                    color = Color.Gray
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
                                text = discount.name + (discount.percentage?.let { " ($it%)" } ?: ""),
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
                        Text(tax.label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text("¥${tax.amount}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            } else {
                taxAmountToShow?.let { taxAmount ->
                    Row(
                        Modifier.fillMaxWidth().padding(top = 6.dp),
                        Arrangement.SpaceBetween
                    ) {
                        Text("消費税", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text("¥$taxAmount", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
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

        Button(onClick = onClose, Modifier.fillMaxWidth()) { Text("閉じる") }
    }
}

@Composable
fun ReceiptLoadErrorSheet(
    message: String,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("⚠️ 読み込みエラー", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onRetry, modifier = Modifier.weight(1f)) {
                Text("再撮影する")
            }
            TextButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text("閉じる")
            }
        }
    }
}
