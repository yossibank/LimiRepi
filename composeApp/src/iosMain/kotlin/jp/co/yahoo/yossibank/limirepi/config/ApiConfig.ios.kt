package jp.co.yahoo.yossibank.limirepi.config

import platform.Foundation.NSBundle

actual object ApiConfig {
    actual val geminiApiKey: String =
        (NSBundle.mainBundle.infoDictionary?.get("GEMINI_API_KEY") as? String) ?: ""
}
