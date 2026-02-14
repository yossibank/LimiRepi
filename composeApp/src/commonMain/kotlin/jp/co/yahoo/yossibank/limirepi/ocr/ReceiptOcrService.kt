package jp.co.yahoo.yossibank.limirepi.ocr

expect class ReceiptOcrService {
    suspend fun scanReceiptWithAI(imageData: ByteArray): ReceiptData?
}
