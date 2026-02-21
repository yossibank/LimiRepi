package jp.co.yahoo.yossibank.limirepi.feature.fridge.model.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeCategory
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeItem
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeSortType

private val ExpiredRed = Color(0xFFEF5350)
private val ExpiredBackground = Color(0x15EF5350)
private val UrgentOrange = Color(0xFFFF7043)
private val WarningAmber = Color(0xFFFFA726)
private val SafeGreen = Color(0xFF66BB6A)
private val GaugeHigh = Color(0xFF4CAF50)
private val GaugeMedium = Color(0xFFFFB74D)
private val GaugeLow = Color(0xFFEF5350)
private val CategoryHeaderShadow = Color(0x0A000000)

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

    Scaffold(modifier = modifier) { innerPadding ->
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

                        // 食材リスト（アコーディオン・アニメーション付き）
                        categoryItems.forEach { fridgeItem: FridgeItem ->
                            item(key = "${category.ordinal}_${fridgeItem.id}") {
                                AnimatedVisibility(
                                    visible = category !in collapsedCategories,
                                    enter = expandVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ) + fadeIn(
                                        animationSpec = tween(durationMillis = 300)
                                    ),
                                    exit = shrinkVertically(
                                        animationSpec = tween(durationMillis = 200)
                                    ) + fadeOut(
                                        animationSpec = tween(durationMillis = 200)
                                    )
                                ) {
                                    FridgeItemCard(
                                        item = fridgeItem
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

/**
 * カテゴリ別のカードヘッダー（アコーディオン・モダンデザイン・アニメーション付き）
 */
@Composable
private fun CategoryHeader(
    category: FridgeCategory,
    itemCount: Int,
    isCollapsed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isCollapsed) 0f else 180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotationAngle"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = CategoryHeaderShadow,
                spotColor = CategoryHeaderShadow
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                category.color.copy(alpha = 0.12f),
                                category.color.copy(alpha = 0.06f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // カテゴリーアイコンエリア（モダンデザイン）
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        category.color.copy(alpha = 0.8f),
                                        category.color.copy(alpha = 0.6f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.emoji,
                            fontSize = 28.sp
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // カテゴリー名とアイテム数
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "${itemCount}件",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    // 折りたたみアイコン（回転アニメーション）
                    Surface(
                        modifier = Modifier
                            .size(40.dp),
                        shape = CircleShape,
                        color = category.color.copy(alpha = 0.15f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isCollapsed) "展開" else "折り畳む",
                                tint = category.color.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotationAngle)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 食材1件分のカード（モダンデザイン）
 */
@Composable
private fun FridgeItemCard(
    item: FridgeItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isExpired) {
                ExpiredBackground
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 絵文字アイコン（グラデーション背景）
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

            // 食材名 + 期限 + 残量ゲージ
            Column(modifier = Modifier.weight(1f)) {
                // 食材名を1行でしっかり表示
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

                // 期限バッジと残量ゲージの行
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (item.expirationDisplayText.isNotEmpty()) {
                        ExpirationBadge(item)
                        Spacer(Modifier.width(8.dp))
                    }

                    // 残量プログレスバー
                    RemainingGauge(
                        percent = item.remainingPercent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            // 数量表示のみ
            QuantityDisplay(quantity = item.quantity)
        }
    }
}

/**
 * 期限アラートバッジ（モダンデザイン・視認性改善）
 */
@Composable
private fun ExpirationBadge(
    item: FridgeItem,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, prefix) = when {
        item.isExpired -> Triple(
            ExpiredRed.copy(alpha = 0.15f),
            ExpiredRed,
            "！"
        )

        item.isUrgent -> Triple(
            UrgentOrange.copy(alpha = 0.15f),
            UrgentOrange,
            "🔥 "
        )

        item.isWarning -> Triple(
            WarningAmber.copy(alpha = 0.20f),
            WarningAmber.copy(alpha = 0.9f),
            ""
        )

        else -> Triple(
            SafeGreen.copy(alpha = 0.15f),
            SafeGreen,
            ""
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = "$prefix${item.expirationDisplayText}",
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * 残量ゲージ（プログレスバー・モダンデザイン）
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
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(gaugeColor.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                gaugeColor,
                                gaugeColor.copy(alpha = 0.8f)
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
            color = gaugeColor,
            fontSize = 12.sp,
            modifier = Modifier.width(40.dp)
        )
    }
}

/**
 * 数量表示（シンプル）
 */
@Composable
private fun QuantityDisplay(
    quantity: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    ) {
        Text(
            text = "×$quantity",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

/**
 * プレビュー用サンプルデータ
 */
private fun sampleFridgeItems(): List<FridgeItem> = listOf(
    // 葉物野菜
    FridgeItem(
        id = "1",
        name = "ほうれん草",
        category = FridgeCategory.LEAFY_VEGETABLE,
        emoji = "🥬",
        quantity = 1,
        remainingPercent = 50,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "2",
        name = "キャベツ",
        category = FridgeCategory.LEAFY_VEGETABLE,
        emoji = "🥬",
        quantity = 1,
        remainingPercent = 15,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "3",
        name = "レタス",
        category = FridgeCategory.LEAFY_VEGETABLE,
        emoji = "🥗",
        quantity = 1,
        remainingPercent = 90,
        daysUntilExpiration = 3
    ),
    // 根菜
    FridgeItem(
        id = "4",
        name = "人参",
        category = FridgeCategory.ROOT_VEGETABLE,
        emoji = "🥕",
        quantity = 2,
        remainingPercent = 80,
        daysUntilExpiration = 5
    ),
    FridgeItem(
        id = "5",
        name = "玉ねぎ",
        category = FridgeCategory.ROOT_VEGETABLE,
        emoji = "🧅",
        quantity = 4,
        remainingPercent = 100,
        daysUntilExpiration = 20
    ),
    FridgeItem(
        id = "6",
        name = "じゃがいも",
        category = FridgeCategory.ROOT_VEGETABLE,
        emoji = "🥔",
        quantity = 7,
        remainingPercent = 95,
        daysUntilExpiration = 15
    ),
    // 果物
    FridgeItem(
        id = "7",
        name = "りんご",
        category = FridgeCategory.FRUIT,
        emoji = "🍎",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 10
    ),
    FridgeItem(
        id = "8",
        name = "トマト",
        category = FridgeCategory.FRUIT,
        emoji = "🍅",
        quantity = 5,
        remainingPercent = 70,
        daysUntilExpiration = 4
    ),
    FridgeItem(
        id = "9",
        name = "バナナ",
        category = FridgeCategory.FRUIT,
        emoji = "🍌",
        quantity = 6,
        remainingPercent = 55,
        daysUntilExpiration = 2
    ),
    // きのこ類
    FridgeItem(
        id = "10",
        name = "しいたけ",
        category = FridgeCategory.MUSHROOM,
        emoji = "🍄",
        quantity = 8,
        remainingPercent = 65,
        daysUntilExpiration = 3
    ),
    FridgeItem(
        id = "11",
        name = "えのき",
        category = FridgeCategory.MUSHROOM,
        emoji = "🍄",
        quantity = 2,
        remainingPercent = 40,
        daysUntilExpiration = 2
    ),
    // 肉類
    FridgeItem(
        id = "12",
        name = "豚バラ肉",
        category = FridgeCategory.MEAT,
        emoji = "🥩",
        quantity = 1,
        remainingPercent = 10,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "13",
        name = "鶏もも肉",
        category = FridgeCategory.MEAT,
        emoji = "🍗",
        quantity = 1,
        remainingPercent = 85,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "14",
        name = "牛肉薄切り",
        category = FridgeCategory.MEAT,
        emoji = "🥩",
        quantity = 1,
        remainingPercent = 45,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "15",
        name = "豚ひき肉",
        category = FridgeCategory.MEAT,
        emoji = "🥩",
        quantity = 1,
        remainingPercent = 20,
        daysUntilExpiration = 0
    ),
    // 魚介類
    FridgeItem(
        id = "16",
        name = "鮭の切り身",
        category = FridgeCategory.FISH,
        emoji = "🐟",
        quantity = 2,
        remainingPercent = 100,
        daysUntilExpiration = 3
    ),
    FridgeItem(
        id = "17",
        name = "サバ",
        category = FridgeCategory.FISH,
        emoji = "🐟",
        quantity = 2,
        remainingPercent = 75,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "18",
        name = "エビ",
        category = FridgeCategory.FISH,
        emoji = "🦐",
        quantity = 11,
        remainingPercent = 50,
        daysUntilExpiration = 1
    ),
    // 加工肉
    FridgeItem(
        id = "19",
        name = "ハム",
        category = FridgeCategory.PROCESSED_MEAT,
        emoji = "🥓",
        quantity = 8,
        remainingPercent = 60,
        daysUntilExpiration = 7
    ),
    FridgeItem(
        id = "20",
        name = "ベーコン",
        category = FridgeCategory.PROCESSED_MEAT,
        emoji = "🥓",
        quantity = 1,
        remainingPercent = 45,
        daysUntilExpiration = 5
    ),
    // 乳製品
    FridgeItem(
        id = "21",
        name = "牛乳",
        category = FridgeCategory.DAIRY,
        emoji = "🥛",
        quantity = 1,
        remainingPercent = 30,
        daysUntilExpiration = 4
    ),
    FridgeItem(
        id = "22",
        name = "ヨーグルト",
        category = FridgeCategory.DAIRY,
        emoji = "🥛",
        quantity = 4,
        remainingPercent = 80,
        daysUntilExpiration = 7
    ),
    FridgeItem(
        id = "23",
        name = "チーズ",
        category = FridgeCategory.DAIRY,
        emoji = "🧀",
        quantity = 1,
        remainingPercent = 55,
        daysUntilExpiration = 10
    ),
    FridgeItem(
        id = "24",
        name = "バター",
        category = FridgeCategory.DAIRY,
        emoji = "🧈",
        quantity = 1,
        remainingPercent = 25,
        daysUntilExpiration = 30
    ),
    // 卵
    FridgeItem(
        id = "25",
        name = "卵",
        category = FridgeCategory.EGG,
        emoji = "🥚",
        quantity = 6,
        remainingPercent = 60,
        daysUntilExpiration = 14
    ),
    // 豆腐・大豆製品
    FridgeItem(
        id = "26",
        name = "豆腐",
        category = FridgeCategory.TOFU_SOY,
        emoji = "🧈",
        quantity = 2,
        remainingPercent = 40,
        daysUntilExpiration = 3
    ),
    FridgeItem(
        id = "27",
        name = "納豆",
        category = FridgeCategory.TOFU_SOY,
        emoji = "🥢",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 5
    ),
    FridgeItem(
        id = "28",
        name = "油揚げ",
        category = FridgeCategory.TOFU_SOY,
        emoji = "🍲",
        quantity = 4,
        remainingPercent = 70,
        daysUntilExpiration = 4
    ),
    // 作り置き
    FridgeItem(
        id = "29",
        name = "カレー",
        category = FridgeCategory.PREPARED,
        emoji = "🍛",
        quantity = 1,
        remainingPercent = 40,
        daysUntilExpiration = 2
    ),
    FridgeItem(
        id = "30",
        name = "煮物",
        category = FridgeCategory.PREPARED,
        emoji = "🍲",
        quantity = 1,
        remainingPercent = 30,
        daysUntilExpiration = 1
    ),
    FridgeItem(
        id = "31",
        name = "ポテトサラダ",
        category = FridgeCategory.PREPARED,
        emoji = "🥗",
        quantity = 1,
        remainingPercent = 60,
        daysUntilExpiration = 1
    ),
    // 残り物
    FridgeItem(
        id = "32",
        name = "唐揚げ",
        category = FridgeCategory.LEFTOVER,
        emoji = "🍗",
        quantity = 5,
        remainingPercent = 80,
        daysUntilExpiration = 0
    ),
    FridgeItem(
        id = "33",
        name = "ご飯",
        category = FridgeCategory.LEFTOVER,
        emoji = "🍚",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 1
    ),
    // 冷凍食品
    FridgeItem(
        id = "34",
        name = "冷凍うどん",
        category = FridgeCategory.FROZEN_FOOD,
        emoji = "🍜",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 90
    ),
    FridgeItem(
        id = "35",
        name = "冷凍餃子",
        category = FridgeCategory.FROZEN_FOOD,
        emoji = "🥟",
        quantity = 2,
        remainingPercent = 75,
        daysUntilExpiration = 45
    ),
    FridgeItem(
        id = "36",
        name = "アイスクリーム",
        category = FridgeCategory.FROZEN_FOOD,
        emoji = "🍨",
        quantity = 5,
        remainingPercent = 100,
        daysUntilExpiration = 180
    ),
    // 冷凍保存
    FridgeItem(
        id = "37",
        name = "冷凍ブロッコリー",
        category = FridgeCategory.FROZEN_HOMEMADE,
        emoji = "🥦",
        quantity = 1,
        remainingPercent = 50,
        daysUntilExpiration = 60
    ),
    FridgeItem(
        id = "38",
        name = "冷凍ミックスベジタブル",
        category = FridgeCategory.FROZEN_HOMEMADE,
        emoji = "🥕",
        quantity = 1,
        remainingPercent = 40,
        daysUntilExpiration = 30
    ),
    // 調味料
    FridgeItem(
        id = "39",
        name = "醤油",
        category = FridgeCategory.SEASONING,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 20,
        daysUntilExpiration = 180
    ),
    FridgeItem(
        id = "40",
        name = "味噌",
        category = FridgeCategory.SEASONING,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 45,
        daysUntilExpiration = 90
    ),
    FridgeItem(
        id = "41",
        name = "塩",
        category = FridgeCategory.SEASONING,
        emoji = "🧂",
        quantity = 1,
        remainingPercent = 80,
        daysUntilExpiration = 365
    ),
    // ソース・油
    FridgeItem(
        id = "42",
        name = "マヨネーズ",
        category = FridgeCategory.SAUCE_OIL,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 35,
        daysUntilExpiration = 60
    ),
    FridgeItem(
        id = "43",
        name = "ケチャップ",
        category = FridgeCategory.SAUCE_OIL,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 60,
        daysUntilExpiration = 120
    ),
    FridgeItem(
        id = "44",
        name = "オリーブオイル",
        category = FridgeCategory.SAUCE_OIL,
        emoji = "🫙",
        quantity = 1,
        remainingPercent = 50,
        daysUntilExpiration = 200
    ),
    // 飲料
    FridgeItem(
        id = "45",
        name = "オレンジジュース",
        category = FridgeCategory.BEVERAGE,
        emoji = "🧃",
        quantity = 1,
        remainingPercent = 70,
        daysUntilExpiration = 5
    ),
    FridgeItem(
        id = "46",
        name = "コーラ",
        category = FridgeCategory.BEVERAGE,
        emoji = "🥤",
        quantity = 3,
        remainingPercent = 100,
        daysUntilExpiration = 90
    ),
    // その他
    FridgeItem(
        id = "47",
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
