package jp.co.yahoo.yossibank.limirepi.receipt.model

import kotlinx.serialization.Serializable

/**
 * レシート商品のカテゴリー
 */
@Serializable
enum class ReceiptCategory(val displayName: String) {
    FOOD("食料品"),
    DRINK("飲料"),
    SNACK("お菓子"),
    DAILY("日用品"),
    OTHER("その他");

    companion object {
        fun fromString(value: String?): ReceiptCategory {
            return when (value?.lowercase()) {
                "food" -> FOOD
                "drink" -> DRINK
                "snack" -> SNACK
                "daily" -> DAILY
                else -> OTHER
            }
        }
    }
}
