package jp.co.yahoo.yossibank.limirepi.receipt.api.models

import kotlinx.serialization.Serializable

/**
 * Gemini APIリクエストモデル
 */
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

/**
 * Gemini APIレスポンスモデル
 */
@Serializable
data class GeminiResponse(
    val candidates: List<Candidate> = emptyList(),
    val error: GeminiError? = null,
    val promptFeedback: PromptFeedback? = null,
    val usageMetadata: UsageMetadata? = null
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
data class UsageMetadata(
    val promptTokenCount: Int = 0,
    val candidatesTokenCount: Int = 0,
    val totalTokenCount: Int = 0
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
