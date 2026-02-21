package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FridgeEmptyState(
    hasSearchQuery: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (hasSearchQuery) "🔍" else "🧊",
            fontSize = 48.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (hasSearchQuery) "該当する食材が見つかりません" else "冷蔵庫に食材がありません",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (hasSearchQuery) "検索条件を変更してください" else "＋ボタンから食材を登録しましょう",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
@Preview
private fun FridgeEmptyStatePreview() {
    MaterialTheme {
        Column(verticalArrangement = Arrangement.spacedBy(100.dp)) {
            FridgeEmptyState(hasSearchQuery = true)
            FridgeEmptyState(hasSearchQuery = false)
        }
    }
}
