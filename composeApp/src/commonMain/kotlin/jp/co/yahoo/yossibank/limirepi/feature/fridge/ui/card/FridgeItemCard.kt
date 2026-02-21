package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.yahoo.yossibank.limirepi.feature.fridge.data.FridgePreview
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeItem
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.parts.FridgeExpirationBadge
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.parts.FridgeRemainingGauge

@Composable
fun FridgeItemCard(
    item: FridgeItem,
    modifier: Modifier = Modifier
) {
    val accentColor = item.category.color
    val isExpired = item.isExpired

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .then(
                if (isExpired) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(0x60EF5350),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired) {
                Color(0x18EF5350)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = if (isExpired) {
            CardDefaults.cardElevation(defaultElevation = 0.dp)
        } else {
            CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左端: カテゴリカラーストライプ
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.9f),
                                    accentColor.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                Spacer(Modifier.width(12.dp))

                // 絵文字アイコンボックス（56dp）
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.25f),
                                    accentColor.copy(alpha = 0.08f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.emoji,
                        fontSize = 28.sp
                    )
                }

                Spacer(Modifier.width(14.dp))

                // 中央: 食材名 + 賞味期限バッジ
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isExpired) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    if (item.expirationDisplayText.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        FridgeExpirationBadge(item = item)
                    }
                }

                Spacer(Modifier.width(12.dp))

                // 右端: 数量バッジ
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "×${item.quantity}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = accentColor.copy(alpha = 0.9f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // 下部フルウィズ残量ゲージ
            FridgeRemainingGauge(
                percent = item.remainingPercent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
@Preview
private fun FridgeItemCardPreview() {
    MaterialTheme {
        Column {
            FridgeItemCard(item = FridgePreview.items[0])
            FridgeItemCard(item = FridgePreview.items[2])
            FridgeItemCard(item = FridgePreview.items[3])
        }
    }
}
