package jp.co.yahoo.yossibank.limirepi.config

actual object ApiConfig {
    actual val geminiApiKey: String = System.getenv("GEMINI_API_KEY") ?: ""
}
