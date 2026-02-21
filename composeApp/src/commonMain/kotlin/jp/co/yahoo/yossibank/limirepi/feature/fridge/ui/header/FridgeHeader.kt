package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeSortType

data class FridgeHeaderState(
    val searchQuery: String,
    val currentSort: FridgeSortType,
    val showExpiredOnly: Boolean,
    val showLowStockOnly: Boolean
)

data class FridgeHeaderCallbacks(
    val onSearchQueryChange: (String) -> Unit,
    val onSortChange: (FridgeSortType) -> Unit,
    val onExpiredOnlyChange: (Boolean) -> Unit,
    val onLowStockOnlyChange: (Boolean) -> Unit
)

@Composable
fun FridgeHeader(
    state: FridgeHeaderState,
    callbacks: FridgeHeaderCallbacks,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = callbacks.onSearchQueryChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        decorationBox = { innerTextField ->
                            if (state.searchQuery.isEmpty()) {
                                Text(
                                    text = "食材を検索",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clickable(onClick = { showSortMenu = true })
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "ソート",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    FridgeSortType.entries.forEach { sortType ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = sortType.displayName,
                                    fontWeight = if (sortType == state.currentSort) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            },
                            onClick = {
                                callbacks.onSortChange(sortType)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FridgeFilterChip(
                state = FridgeFilterChipState(
                    label = "期限切れのみ",
                    isSelected = state.showExpiredOnly
                ),
                onClick = {
                    callbacks.onExpiredOnlyChange(!state.showExpiredOnly)
                }
            )

            FridgeFilterChip(
                state = FridgeFilterChipState(
                    label = "残量少ない",
                    isSelected = state.showLowStockOnly
                ),
                onClick = {
                    callbacks.onLowStockOnlyChange(!state.showLowStockOnly)
                }
            )
        }
    }
}

@Composable
@Preview
private fun FridgeHeaderPreview() {
    MaterialTheme {
        FridgeHeader(
            state = FridgeHeaderState(
                searchQuery = "",
                currentSort = FridgeSortType.REMAINING,
                showExpiredOnly = false,
                showLowStockOnly = false
            ),
            callbacks = FridgeHeaderCallbacks(
                onSearchQueryChange = {},
                onSortChange = {},
                onExpiredOnlyChange = {},
                onLowStockOnlyChange = {}
            )
        )
    }
}
