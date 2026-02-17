package jp.co.yahoo.yossibank.limirepi.feature.receipt.api

/**
 * Gemini API設定
 * 
 * 使用モデル: gemini-2.5-flash-lite
 * - 最も小型で費用対効果が高いモデル
 * - 無料枠で利用可能（入力・出力トークンともに無料）
 * - レシート解析のような軽量タスクに最適
 * 
 * 料金（有料プランの場合）:
 * - 入力: $0.10 / 100万トークン（画像含む）
 * - 出力: $0.40 / 100万トークン
 * 
 * 代替モデル候補:
 * - gemini-2.5-flash-lite-preview: 同価格、最新機能（ただしプレビュー版）
 * - gemini-2.5-flash: $0.30入力/$2.50出力（より高性能だが高額）
 * 
 * 参考: https://ai.google.dev/gemini-api/docs/pricing?hl=ja
 */
object GeminiApiConfig {
    const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    const val MODEL = "gemini-2.5-flash-lite"

    object Pricing {
        // 無料枠では課金されないが、有料プランでの料金を記載
        const val INPUT_PRICE_PER_MILLION_USD = 0.10
        const val OUTPUT_PRICE_PER_MILLION_USD = 0.40
    }

    object GenerationParams {
        const val TEMPERATURE = 0.1
        const val TOP_K = 32
        const val TOP_P = 1.0
        const val MAX_OUTPUT_TOKENS = 2048
        const val RESPONSE_MIME_TYPE = "application/json"
    }

    object ImageParams {
        const val MIME_TYPE = "image/jpeg"
    }
}
