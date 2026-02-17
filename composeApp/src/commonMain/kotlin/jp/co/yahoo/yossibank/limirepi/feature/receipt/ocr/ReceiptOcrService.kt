package jp.co.yahoo.yossibank.limirepi.feature.receipt.ocr

import jp.co.yahoo.yossibank.limirepi.feature.receipt.model.ReceiptData

expect class ReceiptOcrService() {
    suspend fun scanReceipt(imageData: ByteArray): ReceiptData?
    fun close()
}
