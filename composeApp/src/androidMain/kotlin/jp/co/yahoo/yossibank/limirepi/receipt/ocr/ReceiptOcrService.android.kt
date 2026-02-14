package jp.co.yahoo.yossibank.limirepi.receipt.ocr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.YuvImage
import android.graphics.Rect
import android.util.Base64
import jp.co.yahoo.yossibank.limirepi.config.ApiConfig
import jp.co.yahoo.yossibank.limirepi.receipt.api.GeminiApiClient
import jp.co.yahoo.yossibank.limirepi.receipt.model.ReceiptData
import jp.co.yahoo.yossibank.limirepi.logger.AppLogger
import java.io.ByteArrayOutputStream

actual class ReceiptOcrService {
    private val geminiApiClient = GeminiApiClient(ApiConfig.geminiApiKey)

    actual suspend fun scanReceiptWithAI(imageData: ByteArray): ReceiptData? {
        val base64Image = Base64.encodeToString(imageData, Base64.NO_WRAP)

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

    actual fun close() {
        geminiApiClient.close()
    }
}
