package jp.co.yahoo.yossibank.limirepi.receipt.ocr

import jp.co.yahoo.yossibank.limirepi.receipt.model.ReceiptData

expect class ReceiptOcrService() {
    suspend fun scanReceipt(imageData: ByteArray): ReceiptData?
    fun close()
}
