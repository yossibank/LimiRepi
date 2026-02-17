package jp.co.yahoo.yossibank.limirepi.feature.receipt.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * レシート全体のデータモデル
 */
@Serializable
data class ReceiptData(
    val storeName: String? = null,
    val purchaseDate: String? = null,
    val items: List<ReceiptItem> = emptyList(),
    val subtotalAmount: Int? = null,
    val taxBreakdowns: List<TaxBreakdown> = emptyList(),
    val totalAmount: Int? = null,
    val taxAmount: Int? = null
) {
    /**
     * 総計の検証
     */
    val isValidTotal: Boolean
        get() = totalAmount == items.sumOf { it.price * it.quantity }
}

/**
 * 消費税内訳
 */
@Serializable
data class TaxBreakdown(
    val label: String,
    val amount: Int
)

/**
 * レシート商品のデータモデル
 */
@Serializable
data class ReceiptItem(
    val name: String,
    val price: Int,
    val quantity: Int = 1,
    @SerialName("category")
    private val categoryString: String? = null,
    val discount: Discount? = null
) {
    /**
     * カテゴリー（型安全）
     */
    val category: ReceiptCategory
        get() = ReceiptCategory.fromString(categoryString)

    /**
     * 小計（単価 × 数量）
     */
    val subtotal: Int
        get() = price * quantity

    /**
     * 割引適用後の価格
     */
    val priceAfterDiscount: Int
        get() = (subtotal - discountAmount).coerceAtLeast(0)

    /**
     * 割引額（金額指定が無い場合は割引率から推定）
     */
    val discountAmount: Int
        get() = discount?.amount
            ?: discount?.percentage?.let { percentage -> (subtotal * percentage) / 100 }
            ?: 0

    /**
     * 消費税率（食品系は8%、それ以外は10%）
     */
    val taxRate: Double
        get() = when (category) {
            ReceiptCategory.FOOD, ReceiptCategory.DRINK, ReceiptCategory.SNACK -> 0.08
            else -> 0.10
        }

    /**
     * 消費税額（割引適用後の価格に対して）
     */
    val taxAmount: Int
        get() = (priceAfterDiscount * taxRate).toInt()

    /**
     * 税込価格（割引適用後 + 消費税）
     */
    val priceWithTax: Int
        get() = priceAfterDiscount + taxAmount
}

/**
 * 割引情報
 */
@Serializable
data class Discount(
    val name: String,
    val percentage: Int? = null,
    val amount: Int? = null
)
