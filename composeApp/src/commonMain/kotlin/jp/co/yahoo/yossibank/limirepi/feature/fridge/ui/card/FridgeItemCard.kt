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
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.parts.FridgeQuantity
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.parts.FridgeRemainingGauge

@Composable
fun FridgeItemCard(
    item: FridgeItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .then(
                if (item.isExpired) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = Color(0x80EF5350),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isExpired) {
                Color(0x28EF5350)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = if (item.isExpired) {
            CardDefaults.cardElevation(defaultElevation = 0.dp)
        } else {
            CardDefaults.cardElevation(
                defaultElevation = 1.dp,
                pressedElevation = 2.dp
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (item.expirationDisplayText.isNotEmpty()) {
                        FridgeExpirationBadge(item)
                        Spacer(Modifier.width(8.dp))
                    }

                    FridgeRemainingGauge(
                        percent = item.remainingPercent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            FridgeQuantity(quantity = item.quantity)
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
            FridgeItemCard(item = FridgePreview.items[5])
            FridgeItemCard(item = FridgePreview.items[7])
            FridgeItemCard(item = FridgePreview.items[10])
        }
    }
}
