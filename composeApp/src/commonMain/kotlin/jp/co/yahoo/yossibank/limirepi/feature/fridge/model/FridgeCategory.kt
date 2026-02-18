package jp.co.yahoo.yossibank.limirepi.feature.fridge.model

import androidx.compose.ui.graphics.Color

/**
 * 冷蔵庫の食材カテゴリ
 */
enum class FridgeCategory(
    val displayName: String,
    val emoji: String,
    val sortOrder: Int,
    val color: Color
) {
    // 野菜類
    LEAFY_VEGETABLE("葉物野菜", "🥬", 0, Color(0xFF4CAF50)),
    ROOT_VEGETABLE("根菜", "🥕", 1, Color(0xFFFF9800)),
    FRUIT("果物", "🍎", 2, Color(0xFFE91E63)),
    MUSHROOM("きのこ類", "🍄", 3, Color(0xFF795548)),

    // タンパク質
    MEAT("肉類", "🥩", 4, Color(0xFFD32F2F)),
    FISH("魚介類", "🐟", 5, Color(0xFF2196F3)),
    PROCESSED_MEAT("加工肉", "🍖", 6, Color(0xFF9C27B0)),

    // 乳製品・卵・大豆製品
    DAIRY("乳製品", "🥛", 7, Color(0xFFFFEB3B)),
    EGG("卵", "🥚", 8, Color(0xFFFFC107)),
    TOFU_SOY("豆腐・大豆製品", "🍲", 9, Color(0xFF8BC34A)),

    // 調理済み
    PREPARED("作り置き", "🍱", 10, Color(0xFF00BCD4)),
    LEFTOVER("残り物", "🍽️", 11, Color(0xFF009688)),

    // 冷凍
    FROZEN_FOOD("冷凍食品", "🧊", 12, Color(0xFF03A9F4)),
    FROZEN_HOMEMADE("冷凍保存", "❄️", 13, Color(0xFF00ACC1)),

    // 調味料・その他
    SEASONING("調味料", "🧂", 14, Color(0xFF607D8B)),
    SAUCE_OIL("ソース・油", "🫙", 15, Color(0xFF5D4037)),
    BEVERAGE("飲料", "🥤", 16, Color(0xFFFF5722)),
    OTHER("その他", "🍴", 17, Color(0xFF9E9E9E));
}
