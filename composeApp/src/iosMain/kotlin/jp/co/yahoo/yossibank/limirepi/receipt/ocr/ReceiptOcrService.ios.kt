package jp.co.yahoo.yossibank.limirepi.receipt.ocr

import jp.co.yahoo.yossibank.limirepi.config.ApiConfig
import jp.co.yahoo.yossibank.limirepi.logger.AppLogger
import jp.co.yahoo.yossibank.limirepi.receipt.api.GeminiApiClient
import jp.co.yahoo.yossibank.limirepi.receipt.model.ReceiptData
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

    actual suspend fun scanReceipt(imageData: ByteArray): ReceiptData? {
        val base64Image = imageData.toNSData().base64EncodedStringWithOptions(0u)

        AppLogger.d(TAG, "Calling Gemini API for receipt analysis...")

        return geminiApiClient.analyzeReceipt(base64Image)
            .onSuccess { receiptData ->
                AppLogger.d(TAG, "Receipt analyzed: ${receiptData.items.size} items")
                AppLogger.d(
                    TAG,
                    "Store: ${receiptData.storeName}, Total: ${receiptData.totalAmount}"
                )
            }
            .onFailure { error ->
                AppLogger.e(TAG, "Failed to analyze receipt: ${error.message}")
            }
            .getOrNull()
    }

    actual fun close() {
        geminiApiClient.close()
    }

    @OptIn(BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData {
        return usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
        }
    }

    companion object {
        private const val TAG = "ReceiptOcrService"
    }
}
