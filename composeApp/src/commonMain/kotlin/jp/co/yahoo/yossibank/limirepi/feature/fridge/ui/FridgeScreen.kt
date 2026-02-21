package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.yahoo.yossibank.limirepi.feature.fridge.data.FridgePreview
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeCategory
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeItem
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeSortType
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.FridgeItemCard
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.FridgeItemCategoryHeader
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card.FridgeItemCategoryHeaderState
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.empty.FridgeEmptyState
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.header.FridgeHeader
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.header.FridgeHeaderCallbacks
import jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.header.FridgeHeaderState

private val EnterExpandVertically: EnterTransition = expandVertically(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)

private val ExitExpandVertically: ExitTransition = shrinkVertically(
    animationSpec = tween(
        durationMillis = 200
    )
)

private val FadeIn: EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = 300
    )
)

private var FadeOut: ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = 200
    )
)

private val ItemEnterAnimation = EnterExpandVertically + FadeIn
private val ItemExitAnimation = ExitExpandVertically + FadeOut

@Composable
fun FridgeScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var currentSort by remember { mutableStateOf(FridgeSortType.EXPIRATION) }
    var collapsedCategories by remember { mutableStateOf(setOf<FridgeCategory>()) }
    var selectedCategories by remember { mutableStateOf(setOf<FridgeCategory>()) }
    var showExpiredOnly by remember { mutableStateOf(false) }
    var showLowStockOnly by remember { mutableStateOf(false) }

    val items = remember { FridgePreview.items }

    val filteredItems = remember(
        items,
        searchQuery,
        currentSort,
        selectedCategories,
        showExpiredOnly,
        showLowStockOnly
    ) {
        items.filterAndSort(
            searchQuery = searchQuery,
            selectedCategories = selectedCategories,
            showExpiredOnly = showExpiredOnly,
            showLowStockOnly = showLowStockOnly,
            sortType = currentSort
        )
    }

    val groupedItems = remember(filteredItems) {
        filteredItems.groupByCategory()
    }

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FridgeHeader(
                state = FridgeHeaderState(
                    searchQuery = searchQuery,
                    currentSort = currentSort,
                    showExpiredOnly = showExpiredOnly,
                    showLowStockOnly = showLowStockOnly
                ),
                callbacks = FridgeHeaderCallbacks(
                    onSearchQueryChange = { searchQuery = it },
                    onSortChange = { currentSort = it },
                    onExpiredOnlyChange = { showExpiredOnly = it },
                    onLowStockOnlyChange = { showLowStockOnly = it }
                )
            )

            if (filteredItems.isEmpty()) {
                FridgeEmptyState(
                    hasSearchQuery = searchQuery.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )
            } else {
                FridgeSummaryStrip(items = filteredItems)
                FridgeCategoryList(
                    groupedItems = groupedItems,
                    collapsedCategories = collapsedCategories,
                    onCategoryToggle = { category ->
                        collapsedCategories = collapsedCategories.toggle(category)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun FridgeSummaryStrip(
    items: List<FridgeItem>,
    modifier: Modifier = Modifier
) {
    val expiredCount = items.count { it.isExpired }
    val urgentCount = items.count { it.isUrgent }
    val warningCount = items.count { it.isWarning }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🍱 ${items.size}件",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(1f))

        if (expiredCount > 0) {
            FridgeSummaryChip(text = "☠️ $expiredCount", color = Color(0xFFEF5350))
        }
        if (urgentCount > 0) {
            FridgeSummaryChip(text = "🔥 $urgentCount", color = Color(0xFFFF7043))
        }
        if (warningCount > 0) {
            FridgeSummaryChip(text = "⚠️ $warningCount", color = Color(0xFFFFA726))
        }
    }
}

@Composable
private fun FridgeSummaryChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun FridgeCategoryList(
    groupedItems: Map<FridgeCategory, List<FridgeItem>>,
    collapsedCategories: Set<FridgeCategory>,
    onCategoryToggle: (FridgeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier
    ) {
        groupedItems.forEach { (category, categoryItems) ->
            item(
                key = "header_${category.ordinal}",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                FridgeItemCategoryHeader(
                    state = FridgeItemCategoryHeaderState(
                        category = category,
                        itemCount = categoryItems.count(),
                        isCollapsed = category in collapsedCategories
                    ),
                    onToggle = { onCategoryToggle(category) }
                )
            }

            categoryItems.forEach { fridgeItem ->
                item(key = "${category.ordinal}_${fridgeItem.id}") {
                    AnimatedVisibility(
                        visible = category !in collapsedCategories,
                        enter = ItemEnterAnimation,
                        exit = ItemExitAnimation
                    ) {
                        FridgeItemCard(item = fridgeItem)
                    }
                }
            }
        }
    }
}

private fun List<FridgeItem>.filterAndSort(
    searchQuery: String,
    selectedCategories: Set<FridgeCategory>,
    showExpiredOnly: Boolean,
    showLowStockOnly: Boolean,
    sortType: FridgeSortType
): List<FridgeItem> {
    val filtered = filter { item ->
        val matchesSearch =
            searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategories.isEmpty() || item.category in selectedCategories
        val matchesExpired = !showExpiredOnly || item.isExpired
        val matchesLowStock = !showLowStockOnly || item.remainingPercent <= 30
        matchesSearch && matchesCategory && matchesExpired && matchesLowStock
    }

    return when (sortType) {
        FridgeSortType.EXPIRATION -> filtered.sortedBy { it.daysUntilExpiration }
        FridgeSortType.REMAINING -> filtered.sortedBy { it.remainingPercent }
    }
}

private fun List<FridgeItem>.groupByCategory(): Map<FridgeCategory, List<FridgeItem>> =
    groupBy { it.category }
        .entries
        .sortedBy { it.key.sortOrder }
        .associate { it.key to it.value }

private fun Set<FridgeCategory>.toggle(category: FridgeCategory): Set<FridgeCategory> =
    if (category in this) this - category else this + category

@Composable
@Preview
private fun FridgeScreenPreview() {
    MaterialTheme {
        FridgeScreen()
    }
}
