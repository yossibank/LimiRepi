package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GaugeHigh = Color(0xFF4CAF50)
private val GaugeMedium = Color(0xFFFFB74D)
private val GaugeLow = Color(0xFFEF5350)

@Composable
fun FridgeRemainingGauge(
    percent: Int,
    modifier: Modifier = Modifier
) {
    val fraction = (percent / 100f).coerceIn(0f, 1f)

    val gaugeColor = when {
        percent <= 20 -> GaugeLow
        percent <= 50 -> GaugeMedium
        else -> GaugeHigh
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(gaugeColor.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        horizontalGradient(
                            colors = listOf(
                                gaugeColor,
                                gaugeColor.copy(alpha = 0.75f)
                            )
                        )
                    )
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = "${percent}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = gaugeColor
        )
    }
}


@Composable
@Preview
private fun FridgeRemainingPreview() {
    MaterialTheme {
        Column {
            FridgeRemainingGauge(percent = 20)
            FridgeRemainingGauge(percent = 50)
            FridgeRemainingGauge(percent = 80)
        }
    }
}
