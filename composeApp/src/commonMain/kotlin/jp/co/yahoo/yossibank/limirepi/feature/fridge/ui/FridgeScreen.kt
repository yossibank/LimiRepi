package jp.co.yahoo.yossibank.limirepi.feature.fridge.model.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeCategory
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeItem
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeSortType

// region Colors
private val ExpiredRed = Color(0xFFD32F2F)
private val ExpiredBackground = Color(0x1AD32F2F)
private val UrgentOrange = Color(0xFFE65100)
private val WarningAmber = Color(0xFFF57C00)
private val SafeGreen = Color(0xFF4CAF50)
private val GaugeHigh = Color(0xFF4CAF50)
private val GaugeMedium = Color(0xFFFFC107)
private val GaugeLow = Color(0xFFD32F2F)
private val CategoryHeaderBackground = Color(0xFFF5F5F5)
// endregion

/**
 * 冷蔵庫画面（カテゴリ別リスト形式）
 */
@Composable
fun FridgeScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentSort by remember { mutableStateOf(FridgeSortType.EXPIRATION) }
    var collapsedCategories by remember { mutableStateOf(setOf<FridgeCategory>()) }
    var selectedCategories by remember { mutableStateOf(setOf<FridgeCategory>()) }
    var showExpiredOnly by remember { mutableStateOf(false) }
    var showLowStockOnly by remember { mutableStateOf(false) }

    // サンプルデータ使用
    val items = remember { sampleFridgeItems() }
    val onQuantityChange: (String, Int) -> Unit = { _, _ -> }

    val filteredItems = remember(
        items,
        searchQuery,
        currentSort,
        selectedCategories,
        showExpiredOnly,
        showLowStockOnly
    ) {
        items
            .filter { item ->
                // 検索フィルタ
                val matchesSearch =
                    searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true)

                // カテゴリフィルタ
                val matchesCategory =
                    selectedCategories.isEmpty() || item.category in selectedCategories

                // 期限切れフィルタ
                val matchesExpired = !showExpiredOnly || item.isExpired

                // 残量少ないフィルタ
                val matchesLowStock = !showLowStockOnly || item.remainingPercent <= 30

                matchesSearch && matchesCategory && matchesExpired && matchesLowStock
            }
            .let { list ->
                when (currentSort) {
                    FridgeSortType.EXPIRATION -> list.sortedBy { it.daysUntilExpiration }
                    FridgeSortType.REMAINING -> list.sortedBy { it.remainingPercent }
                }
            }
    }

    val groupedItems: Map<FridgeCategory, List<FridgeItem>> = remember(filteredItems) {
        filteredItems
            .groupBy { it.category }
            .entries
            .sortedBy { it.key.sortOrder }
            .associate { it.key to it.value }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "食材を追加"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ヘッダー: 検索バー + ソート/フィルタ
            FridgeHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                currentSort = currentSort,
                onSortChange = { currentSort = it },
                showExpiredOnly = showExpiredOnly,
                onExpiredOnlyChange = { showExpiredOnly = it },
                showLowStockOnly = showLowStockOnly,
                onLowStockOnlyChange = { showLowStockOnly = it }
            )

            if (filteredItems.isEmpty()) {
                FridgeEmptyState(
                    hasSearchQuery = searchQuery.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )
            } else {
                // カテゴリ別リスト
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedItems.forEach { (category: FridgeCategory, categoryItems: List<FridgeItem>) ->
                        // カテゴリヘッダー
                        item(key = "header_${category.ordinal}") {
                            CategoryHeader(
                                category = category,
                                itemCount = categoryItems.count(),
                                isCollapsed = category in collapsedCategories,
                                onToggle = {
                                    collapsedCategories = if (category in collapsedCategories) {
                                        collapsedCategories - category
                                    } else {
                                        collapsedCategories + category
                                    }
                                }
                            )
                        }

                        // 食材リスト（アコーディオン）
                        if (category !in collapsedCategories) {
                            categoryItems.forEach { fridgeItem: FridgeItem ->
                                item(key = fridgeItem.id) {
                                    FridgeItemCard(
                                        item = fridgeItem,
                                        onQuantityChange = { newQuantity ->
                                            onQuantityChange(fridgeItem.id, newQuantity)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// region ヘッダー

/**
 * 検索バー + ソート/フィルタボタン
 */
@Composable
private fun FridgeHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentSort: FridgeSortType,
    onSortChange: (FridgeSortType) -> Unit,
    showExpiredOnly: Boolean,
    onExpiredOnlyChange: (Boolean) -> Unit,
    showLowStockOnly: Boolean,
    onLowStockOnlyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("食材を検索") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(Modifier.width(8.dp))

            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "ソート",
                        tint = MaterialTheme.colorScheme.primary
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
                                    fontWeight = if (sortType == currentSort) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            },
                            onClick = {
                                onSortChange(sortType)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // フィルタチップ表示エリア
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = "期限切れのみ",
                isSelected = showExpiredOnly,
                onClick = { onExpiredOnlyChange(!showExpiredOnly) }
            )

            FilterChip(
                label = "残量少ない",
                isSelected = showLowStockOnly,
                onClick = { onLowStockOnlyChange(!showLowStockOnly) }
            )
        }
    }
}

/**
 * フィルタチップ
 */
@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

// endregion

// region 空状態

/**
 * 食材がない場合の空状態UI
 */
@Composable
private fun FridgeEmptyState(
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

// endregion

// region カテゴリヘッダー

/**
 * カテゴリ別のスティッキーヘッダー（アコーディオン）
 */
@Composable
private fun CategoryHeader(
    category: FridgeCategory,
    itemCount: Int,
    isCollapsed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CategoryHeaderBackground)
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.emoji,
            fontSize = 20.sp
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = category.displayName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "($itemCount)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.width(4.dp))

        Icon(
            imageVector = if (isCollapsed) {
                Icons.Default.KeyboardArrowDown
            } else {
                Icons.Default.KeyboardArrowUp
            },
            contentDescription = if (isCollapsed) "展開" else "折り畳む",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// endregion

// region 食材カード

/**
 * 食材1件分のカード
 */
@Composable
private fun FridgeItemCard(
    item: FridgeItem,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (item.isExpired) ExpiredBackground else Color.Transparent

    Column(modifier = modifier.background(cardBackground)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 絵文字アイコン
            Text(
                text = item.emoji,
                fontSize = 32.sp
            )

            Spacer(Modifier.width(12.dp))

            // 食材名 + 期限 + 残量ゲージ
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (item.expirationDisplayText.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        ExpirationBadge(item)
                    }
                }

                Spacer(Modifier.height(6.dp))

                // 残量プログレスバー
                RemainingGauge(
                    percent = item.remainingPercent,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.width(12.dp))

            // 数量 ±ボタン
            QuantityControls(
                quantity = item.quantity,
                onQuantityChange = onQuantityChange
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

/**
 * 期限アラートバッジ
 */
@Composable
private fun ExpirationBadge(
    item: FridgeItem,
    modifier: Modifier = Modifier
) {
    val (textColor, prefix) = when {
        item.isExpired -> ExpiredRed to "！"
        item.isUrgent -> UrgentOrange to "🔥 "
        item.isWarning -> WarningAmber to ""
        else -> SafeGreen to ""
    }

    Text(
        text = "$prefix${item.expirationDisplayText}",
        style = MaterialTheme.typography.labelMedium,
        color = textColor,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

/**
 * 残量ゲージ（プログレスバー）
 */
@Composable
private fun RemainingGauge(
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
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .width(100.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = gaugeColor,
            trackColor = gaugeColor.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "${percent}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(35.dp)
        )
    }
}

/**
 * 数量 ＋/− コントロール
 */
@Composable
private fun QuantityControls(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (quantity > 0) onQuantityChange(quantity - 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "減らす",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "$quantity",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "増やす",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// endregion

// region Preview

/**
 * プレビュー用サンプルデータ
 */
private fun sampleFridgeItems(): List<FridgeItem> = listOf(
    // 野菜・果物
    FridgeItem(
        id = "1",
        name = "ほうれん草",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🥬",
        quantity = 1,
        remainingPercent = 50,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "2",
        name = "人参",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🥕",
        quantity = 2,
        remainingPercent = 80,
        daysUntilExpiration = 5
    ),
    FridgeItem(
        id = "3",
        name = "りんご",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🍎",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 10
    ),
    FridgeItem(
        id = "11",
        name = "トマト",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🍅",
        quantity = 5,
        remainingPercent = 70,
        daysUntilExpiration = 4
    ),
    FridgeItem(
        id = "12",
        name = "キャベツ",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🥬",
        quantity = 1,
        remainingPercent = 15,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "13",
        name = "レタス",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🥗",
        quantity = 1,
        remainingPercent = 90,
        daysUntilExpiration = 3
    ),
    FridgeItem(
        id = "14",
        name = "バナナ",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🍌",
        quantity = 6,
        remainingPercent = 55,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "15",
        name = "玉ねぎ",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🧅",
        quantity = 4,
        remainingPercent = 100,
        daysUntilExpiration = 20
    ),
    FridgeItem(
        id = "16",
        name = "じゃがいも",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🥔",
        quantity = 7,
        remainingPercent = 95,
        daysUntilExpiration = 15
    ),
    FridgeItem(
        id = "17",
        name = "ピーマン",
        category = FridgeCategory.VEGETABLE_FRUIT,
        emoji = "🫑",
        quantity = 3,
        remainingPercent = 65,
        daysUntilExpiration = 5
    ),
    // 肉・魚
    FridgeItem(
        id = "4",
        name = "豚バラ肉",
        category = FridgeCategory.MEAT_FISH,
        emoji = "🥩",
        quantity = 1,
        remainingPercent = 10,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "5",
        name = "鮭の切り身",
        category = FridgeCategory.MEAT_FISH,
        emoji = "🐟",
        quantity = 2,
        remainingPercent = 100,
        daysUntilExpiration = 3
    ),
    FridgeItem(
        id = "18",
        name = "鶏もも肉",
        category = FridgeCategory.MEAT_FISH,
        emoji = "🍗",
        quantity = 1,
        remainingPercent = 85,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "19",
        name = "牛肉薄切り",
        category = FridgeCategory.MEAT_FISH,
        emoji = "🥩",
        quantity = 1,
        remainingPercent = 45,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "20",
        name = "サバ",
        category = FridgeCategory.MEAT_FISH,
        emoji = "🐟",
        quantity = 2,
        remainingPercent = 75,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "21",
        name = "豚ひき肉",
        category = FridgeCategory.MEAT_FISH,
        emoji = "🥩",
        quantity = 1,
        remainingPercent = 20,
        daysUntilExpiration = 0
    ),
    FridgeItem(
        id = "22",
        name = "エビ",
        category = FridgeCategory.MEAT_FISH,
        emoji = "🦐",
        quantity = 11,
        remainingPercent = 50,
        daysUntilExpiration = 1
    ),
    // 乳製品・卵・豆腐
    FridgeItem(
        id = "6",
        name = "牛乳",
        category = FridgeCategory.DAIRY_EGG_TOFU,
        emoji = "🥛",
        quantity = 1,
        remainingPercent = 30,
        daysUntilExpiration = 4
    ),
    FridgeItem(
        id = "7",
        name = "卵",
        category = FridgeCategory.DAIRY_EGG_TOFU,
        emoji = "🥚",
        quantity = 6,
        remainingPercent = 60,
        daysUntilExpiration = 14
    ),
    FridgeItem(
        id = "23",
        name = "豆腐",
        category = FridgeCategory.DAIRY_EGG_TOFU,
        emoji = "🧈",
        quantity = 2,
        remainingPercent = 40,
        daysUntilExpiration = 3
    ),
    FridgeItem(
        id = "24",
        name = "ヨーグルト",
        category = FridgeCategory.DAIRY_EGG_TOFU,
        emoji = "🥛",
        quantity = 4,
        remainingPercent = 80,
        daysUntilExpiration = 7
    ),
    FridgeItem(
        id = "25",
        name = "チーズ",
        category = FridgeCategory.DAIRY_EGG_TOFU,
        emoji = "🧀",
        quantity = 1,
        remainingPercent = 55,
        daysUntilExpiration = 10
    ),
    FridgeItem(
        id = "26",
        name = "バター",
        category = FridgeCategory.DAIRY_EGG_TOFU,
        emoji = "🧈",
        quantity = 1,
        remainingPercent = 25,
        daysUntilExpiration = 30
    ),
    FridgeItem(
        id = "27",
        name = "納豆",
        category = FridgeCategory.DAIRY_EGG_TOFU,
        emoji = "🥢",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 5
    ),
    // 調味料
    FridgeItem(
        id = "10",
        name = "醤油",
        category = FridgeCategory.SEASONING,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 20,
        daysUntilExpiration = 180
    ),
    FridgeItem(
        id = "28",
        name = "味噌",
        category = FridgeCategory.SEASONING,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 45,
        daysUntilExpiration = 90
    ),
    FridgeItem(
        id = "29",
        name = "マヨネーズ",
        category = FridgeCategory.SEASONING,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 35,
        daysUntilExpiration = 60
    ),
    FridgeItem(
        id = "30",
        name = "ケチャップ",
        category = FridgeCategory.SEASONING,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 60,
        daysUntilExpiration = 120
    ),
    FridgeItem(
        id = "31",
        name = "ポン酢",
        category = FridgeCategory.SEASONING,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 70,
        daysUntilExpiration = 150
    ),
    // 冷凍食品
    FridgeItem(
        id = "9",
        name = "冷凍うどん",
        category = FridgeCategory.FROZEN,
        emoji = "🍜",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 90
    ),
    FridgeItem(
        id = "32",
        name = "冷凍ブロッコリー",
        category = FridgeCategory.FROZEN,
        emoji = "🥦",
        quantity = 1,
        remainingPercent = 50,
        daysUntilExpiration = 60
    ),
    FridgeItem(
        id = "33",
        name = "冷凍餃子",
        category = FridgeCategory.FROZEN,
        emoji = "🥟",
        quantity = 2,
        remainingPercent = 75,
        daysUntilExpiration = 45
    ),
    FridgeItem(
        id = "34",
        name = "アイスクリーム",
        category = FridgeCategory.FROZEN,
        emoji = "🍨",
        quantity = 5,
        remainingPercent = 100,
        daysUntilExpiration = 180
    ),
    FridgeItem(
        id = "35",
        name = "冷凍ミックスベジタブル",
        category = FridgeCategory.FROZEN,
        emoji = "🥕",
        quantity = 1,
        remainingPercent = 40,
        daysUntilExpiration = 30
    ),
    // 調理済み
    FridgeItem(
        id = "8",
        name = "カレーの残り",
        category = FridgeCategory.PREPARED,
        emoji = "🍛",
        quantity = 1,
        remainingPercent = 40,
        daysUntilExpiration = -1
    ),
    FridgeItem(
        id = "36",
        name = "煮物",
        category = FridgeCategory.PREPARED,
        emoji = "🍲",
        quantity = 1,
        remainingPercent = 30,
        daysUntilExpiration = 0
    ),
    FridgeItem(
        id = "37",
        name = "ポテトサラダ",
        category = FridgeCategory.PREPARED,
        emoji = "🥗",
        quantity = 1,
        remainingPercent = 60,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "38",
        name = "唐揚げ",
        category = FridgeCategory.PREPARED,
        emoji = "🍗",
        quantity = 5,
        remainingPercent = 80,
        daysUntilExpiration = 1
    ),
    // その他
    FridgeItem(
        id = "39",
        name = "パン",
        category = FridgeCategory.OTHER,
        emoji = "🍞",
        quantity = 1,
        remainingPercent = 50,
        daysUntilExpiration = 3
    ),
    FridgeItem(
        id = "40",
        name = "ジャム",
        category = FridgeCategory.OTHER,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 45,
        daysUntilExpiration = 90
    ),
    FridgeItem(
        id = "41",
        name = "ハム",
        category = FridgeCategory.OTHER,
        emoji = "🥓",
        quantity = 1,
        remainingPercent = 20,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "42",
        name = "わかめ",
        category = FridgeCategory.OTHER,
        emoji = "🥬",
        quantity = 1,
        remainingPercent = 90,
        daysUntilExpiration = 120
    )
)

// endregion
