package jp.co.yahoo.yossibank.limirepi.ocr

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptData(
    val storeName: String? = null,
    val purchaseDate: String? = null,
    val items: List<ReceiptItem> = emptyList(),
    val totalAmount: Int? = null,
    val taxAmount: Int? = null
)

@Serializable
data class ReceiptItem(
    val name: String,
    val price: Int,
    val quantity: Int = 1,
    val category: String? = null
)
