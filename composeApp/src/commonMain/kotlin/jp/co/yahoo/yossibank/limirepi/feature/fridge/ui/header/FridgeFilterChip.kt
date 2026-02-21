package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class FridgeFilterChipState(
    val label: String,
    val isSelected: Boolean
)

@Composable
fun FridgeFilterChip(
    state: FridgeFilterChipState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(
                if (state.isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                }
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = state.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (state.isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (state.isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
@Preview
private fun FridgeFilterChipPreview() {
    MaterialTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FridgeFilterChip(
                state = FridgeFilterChipState(
                    label = "期限切れのみ",
                    isSelected = true
                ),
                onClick = {}
            )

            FridgeFilterChip(
                state = FridgeFilterChipState(
                    label = "残量少ない",
                    isSelected = false
                ),
                onClick = {}
            )
        }
    }
}
