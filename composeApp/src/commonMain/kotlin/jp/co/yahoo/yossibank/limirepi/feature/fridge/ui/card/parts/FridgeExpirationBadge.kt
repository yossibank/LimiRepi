package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.parts

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.yahoo.yossibank.limirepi.feature.fridge.data.FridgePreview
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeItem

private val ExpiredRed = Color(0xFFEF5350)
private val UrgentOrange = Color(0xFFFF7043)
private val WarningAmber = Color(0xFFFFA726)
private val SafeGreen = Color(0xFF66BB6A)

@Composable
fun FridgeExpirationBadge(
    item: FridgeItem,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        item.isExpired -> ExpiredRed.copy(alpha = 0.2f)
        item.isUrgent -> UrgentOrange.copy(alpha = 0.15f)
        item.isWarning -> WarningAmber.copy(alpha = 0.2f)
        else -> SafeGreen.copy(alpha = 0.15f)
    }

    val textColor = when {
        item.isExpired -> ExpiredRed
        item.isUrgent -> UrgentOrange
        item.isWarning -> WarningAmber.copy(alpha = 0.9f)
        else -> SafeGreen
    }

    val prefix = when {
        item.isExpired -> "☠️ "
        item.isUrgent -> "🔥 "
        item.isWarning -> ""
        else -> ""
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        modifier = modifier.then(
            if (item.isExpired) {
                Modifier.border(
                    width = 1.dp,
                    color = ExpiredRed.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
            } else {
                Modifier
            }
        )
    ) {
        Text(
            text = "$prefix${item.expirationDisplayText}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
@Preview
private fun FridgeExpirationBadgePreview() {
    MaterialTheme {
        Column {
            FridgeExpirationBadge(item = FridgePreview.items[0])
            FridgeExpirationBadge(item = FridgePreview.items[3])
            FridgeExpirationBadge(item = FridgePreview.items[5])
        }
    }
}
