package jp.co.yahoo.yossibank.limirepi.feature.receipt.ocr

import android.util.Base64
import jp.co.yahoo.yossibank.limirepi.config.ApiConfig
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.GeminiApiClient
import jp.co.yahoo.yossibank.limirepi.feature.receipt.model.ReceiptData
import jp.co.yahoo.yossibank.limirepi.util.logger.AppLogger

actual class ReceiptOcrService {
    private val geminiApiClient = GeminiApiClient(ApiConfig.geminiApiKey)

    actual suspend fun scanReceipt(imageData: ByteArray): ReceiptData? {
        val base64Image = Base64.encodeToString(imageData, Base64.NO_WRAP)

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

    companion object {
        private const val TAG = "ReceiptOcrService"
    }
}
