package jp.co.yahoo.yossibank.limirepi.ocr

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import jp.co.yahoo.yossibank.limirepi.AppLogger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

    suspend fun analyzeReceipt(imageBase64: String): ReceiptData? {
        return try {
            val prompt = buildPrompt()
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt),
                            Part(inlineData = InlineData(
                                mimeType = "image/jpeg",
                                data = imageBase64
                            ))
                        )
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.1,
                    topK = 32,
                    topP = 1.0,
                    maxOutputTokens = 2048,
                    responseMimeType = "application/json"
                )
            )

            AppLogger.d("GeminiApi", "Sending request to Gemini API...")

            val response: GeminiResponse = httpClient.post(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=$apiKey"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            // エラーチェック
            response.error?.let { error ->
                AppLogger.e("GeminiApi", "API error: ${error.message} (code: ${error.code})")
                return null
            }

            parseResponse(response)
        } catch (e: Exception) {
            AppLogger.e("GeminiApi", "Error analyzing receipt: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun buildPrompt(): String {
        return """
あなたはレシート解析の専門家です。以下の画像からレシート情報を抽出し、JSON形式で返してください。

以下のJSON形式で出力してください：
{
  "storeName": "店舗名（見つからない場合はnull）",
  "purchaseDate": "購入日（YYYY-MM-DD形式、見つからない場合はnull）",
  "items": [
    {
      "name": "商品名",
      "price": 価格（整数）,
      "quantity": 個数（整数、デフォルト1）,
      "category": "カテゴリ（food/drink/snack/daily/other、推測できない場合はnull）"
    }
  ],
  "totalAmount": 合計金額（整数、見つからない場合はnull）,
  "taxAmount": 消費税額（整数、見つからない場合はnull）
}

重要な注意事項：
- 商品名は正確に抽出してください
- 価格は必ず整数で返してください
- 合計金額、小計、税込み金額などを区別してください
- 日付はYYYY-MM-DD形式に変換してください（例：2024/01/15 → 2024-01-15）
- カテゴリは商品名から推測してください
- 見つからない情報はnullにしてください
- JSON形式のみを返し、他の説明は不要です
        """.trimIndent()
    }

    private fun parseResponse(response: GeminiResponse): ReceiptData? {
        // プロンプトがブロックされた場合
        response.promptFeedback?.blockReason?.let { reason ->
            AppLogger.e("GeminiApi", "Prompt blocked: $reason")
            return null
        }

        val jsonText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        if (jsonText == null) {
            AppLogger.e("GeminiApi", "No text in response")
            return null
        }

        return try {
            jsonFormat.decodeFromString<ReceiptData>(jsonText)
        } catch (e: Exception) {
            AppLogger.e("GeminiApi", "Error parsing receipt data: ${e.message}")
            AppLogger.d("GeminiApi", "Raw response: $jsonText")
            null
        }
    }

    fun close() {
        httpClient.close()
    }
}

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GenerationConfig(
    val temperature: Double,
    val topK: Int,
    val topP: Double,
    val maxOutputTokens: Int,
    val responseMimeType: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate> = emptyList(),
    val error: GeminiError? = null,
    val promptFeedback: PromptFeedback? = null
)

@Serializable
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

@Serializable
data class PromptFeedback(
    val blockReason: String? = null
)

@Serializable
data class Candidate(
    val content: ContentResponse
)

@Serializable
data class ContentResponse(
    val parts: List<PartResponse>
)

@Serializable
data class PartResponse(
    val text: String
)
