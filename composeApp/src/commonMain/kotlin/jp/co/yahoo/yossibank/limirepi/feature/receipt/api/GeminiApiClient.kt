package jp.co.yahoo.yossibank.limirepi.feature.receipt.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.Candidate
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.Content
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.GeminiRequest
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.GeminiResponse
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.GenerationConfig
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.InlineData
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.Part
import jp.co.yahoo.yossibank.limirepi.feature.receipt.api.models.UsageMetadata
import jp.co.yahoo.yossibank.limirepi.feature.receipt.model.ReceiptData
import jp.co.yahoo.yossibank.limirepi.util.logger.AppLogger
import kotlinx.serialization.json.Json
import kotlin.math.round

/**
 * Gemini APIクライアント
 * レシート画像解析用のAPI通信を担当
 */
class GeminiApiClient(private val apiKey: String) {

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(jsonFormat)
        }
    }

    /**
     * レシート画像を解析
     * @param imageBase64 Base64エンコードされた画像データ
     * @return 解析結果、エラー時はnull
     */
    suspend fun analyzeReceipt(imageBase64: String): Result<ReceiptData> {
        return try {
            val request = buildRequest(imageBase64)

            AppLogger.d(TAG, "Sending request to Gemini API...")

            val response: GeminiResponse = httpClient.post(buildUrl()) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            logUsageAndCost(response.usageMetadata)
            handleResponse(response)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error analyzing receipt: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun buildUrl(): String {
        return "${GeminiApiConfig.BASE_URL}/models/${GeminiApiConfig.MODEL}:generateContent?key=$apiKey"
    }

    private fun buildRequest(imageBase64: String): GeminiRequest {
        return GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = PromptBuilder.buildReceiptAnalysisPrompt()),
                        Part(
                            inlineData = InlineData(
                                mimeType = GeminiApiConfig.ImageParams.MIME_TYPE,
                                data = imageBase64
                            )
                        )
                    )
                )
            ),
            generationConfig = GenerationConfig(
                temperature = GeminiApiConfig.GenerationParams.TEMPERATURE,
                topK = GeminiApiConfig.GenerationParams.TOP_K,
                topP = GeminiApiConfig.GenerationParams.TOP_P,
                maxOutputTokens = GeminiApiConfig.GenerationParams.MAX_OUTPUT_TOKENS,
                responseMimeType = GeminiApiConfig.GenerationParams.RESPONSE_MIME_TYPE
            )
        )
    }

    private fun handleResponse(response: GeminiResponse): Result<ReceiptData> {
        response.error?.let { error ->
            val errorMessage = "API error: ${error.message} (code: ${error.code})"
            AppLogger.e(TAG, errorMessage)
            return Result.failure(GeminiApiException(errorMessage))
        }

        response.promptFeedback?.blockReason?.let { reason ->
            val errorMessage = "Prompt blocked: $reason"
            AppLogger.e(TAG, errorMessage)
            return Result.failure(PromptBlockedException(reason))
        }

        return parseReceiptData(response.candidates)
    }

    private fun parseReceiptData(candidates: List<Candidate>): Result<ReceiptData> {
        val jsonText = candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

        if (jsonText == null) {
            AppLogger.e(TAG, "No text in response")
            return Result.failure(NoResponseException())
        }

        return try {
            val receiptData = jsonFormat.decodeFromString<ReceiptData>(jsonText)
            Result.success(receiptData)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error parsing receipt data: ${e.message}")
            AppLogger.d(TAG, "Raw response: $jsonText")
            Result.failure(ParseException(jsonText, e))
        }
    }

    private fun logUsageAndCost(usageMetadata: UsageMetadata?) {
        if (usageMetadata == null) {
            AppLogger.w(TAG, "Gemini usage metadata is unavailable")
            return
        }

        val inputTokens = usageMetadata.promptTokenCount
        val outputTokens = usageMetadata.candidatesTokenCount
        val totalTokens = if (usageMetadata.totalTokenCount > 0) {
            usageMetadata.totalTokenCount
        } else {
            inputTokens + outputTokens
        }

        val inputCostUsd =
            (inputTokens / TOKENS_PER_MILLION) * GeminiApiConfig.Pricing.INPUT_PRICE_PER_MILLION_USD
        val outputCostUsd =
            (outputTokens / TOKENS_PER_MILLION) * GeminiApiConfig.Pricing.OUTPUT_PRICE_PER_MILLION_USD
        val totalCostUsd = inputCostUsd + outputCostUsd
        val totalCostJpy = totalCostUsd * USD_TO_JPY_RATE
        val formattedTotalCostJpy = formatYenAmount(totalCostJpy)

        AppLogger.i(
            TAG,
            "Gemini usage: input=$inputTokens, output=$outputTokens, total=$totalTokens, estimatedCostJpy=${formattedTotalCostJpy}円"
        )
    }

    private fun formatYenAmount(costYen: Double): String {
        val scaled = round(costYen * COST_DECIMAL_SCALE).toLong()
        val integerPart = scaled / COST_DECIMAL_SCALE_LONG
        val fractionPart =
            (scaled % COST_DECIMAL_SCALE_LONG).toString().padStart(COST_DECIMAL_DIGITS, '0')
        return "$integerPart.$fractionPart"
    }

    fun close() {
        httpClient.close()
    }

    companion object {
        private const val TAG = "GeminiApi"
        private const val TOKENS_PER_MILLION = 1_000_000.0
        private const val COST_DECIMAL_SCALE = 1_000_000.0
        private const val COST_DECIMAL_SCALE_LONG = 1_000_000L
        private const val COST_DECIMAL_DIGITS = 8
        private const val USD_TO_JPY_RATE = 155.0
    }
}

/**
 * Gemini API関連の例外
 */
sealed class GeminiException(message: String, cause: Throwable? = null) : Exception(message, cause)

class GeminiApiException(message: String) : GeminiException(message)
class PromptBlockedException(blockReason: String) : GeminiException("Prompt blocked: $blockReason")
class NoResponseException : GeminiException("No response from API")
class ParseException(val rawResponse: String, cause: Throwable) :
    GeminiException("Failed to parse response", cause)
