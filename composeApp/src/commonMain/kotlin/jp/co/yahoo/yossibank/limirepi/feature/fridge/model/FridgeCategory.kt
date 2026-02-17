package jp.co.yahoo.yossibank.limirepi.feature.fridge.model

/**
 * 冷蔵庫の食材カテゴリ
 */
enum class FridgeCategory(
    val displayName: String,
    val emoji: String,
    val sortOrder: Int
) {
    VEGETABLE_FRUIT("野菜・果物", "🥬", 0),
    MEAT_FISH("肉・魚介", "🥩", 1),
    DAIRY_EGG_TOFU("乳製品・卵・豆腐", "🥚", 2),
    PREPARED("作り置き・残り物", "🍱", 3),
    FROZEN("冷凍食品", "🧊", 4),
    SEASONING("調味料", "🧂", 5),
    OTHER("その他", "🍴", 6);
}
