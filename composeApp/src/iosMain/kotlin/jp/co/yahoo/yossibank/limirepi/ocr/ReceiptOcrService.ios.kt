package jp.co.yahoo.yossibank.limirepi.ocr

import jp.co.yahoo.yossibank.limirepi.AppLogger
import jp.co.yahoo.yossibank.limirepi.config.ApiConfig
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class)
actual class ReceiptOcrService {

    private val geminiApiClient = GeminiApiClient(ApiConfig.geminiApiKey)

    actual suspend fun scanReceiptWithAI(imageData: ByteArray): ReceiptData? {
        val base64Image = imageData.toNSData().base64EncodedStringWithOptions(0u)

        AppLogger.d("ReceiptOcrService", "Calling Gemini API for receipt analysis...")

        val receiptData = geminiApiClient.analyzeReceipt(base64Image)

        if (receiptData != null) {
            AppLogger.d("ReceiptOcrService", "Receipt analyzed: ${receiptData.items.size} items")
            AppLogger.d(
                "ReceiptOcrService",
                "Store: ${receiptData.storeName}, Total: ${receiptData.totalAmount}"
            )
        } else {
            AppLogger.e("ReceiptOcrService", "Failed to analyze receipt with Gemini API")
        }

        return receiptData
    }

    @OptIn(BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData {
        return usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
        }
    }
}
