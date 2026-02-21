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
            .background(
                if (state.isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = state.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (state.isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
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
