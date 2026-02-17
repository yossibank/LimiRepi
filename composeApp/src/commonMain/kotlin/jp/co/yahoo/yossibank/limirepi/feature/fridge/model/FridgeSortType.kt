package jp.co.yahoo.yossibank.limirepi.feature.fridge.model

/**
 * 冷蔵庫一覧のソート種別
 */
enum class FridgeSortType(val displayName: String) {
    EXPIRATION("期限が近い順"),
    REMAINING("残量が少ない順")
}
