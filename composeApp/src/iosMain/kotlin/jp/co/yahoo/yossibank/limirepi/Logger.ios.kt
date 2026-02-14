package jp.co.yahoo.yossibank.limirepi

import platform.Foundation.NSLog

internal class IosLogger : Logger {
    override fun debug(tag: String, message: String) {
        NSLog("[$tag] DEBUG: $message")
    }

    override fun info(tag: String, message: String) {
        NSLog("[$tag] INFO: $message")
    }

    override fun warning(tag: String, message: String) {
        NSLog("[$tag] WARNING: $message")
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            NSLog("[$tag] ERROR: $message\n${throwable.stackTraceToString()}")
        } else {
            NSLog("[$tag] ERROR: $message")
        }
    }
}

actual fun createLogger(): Logger = IosLogger()
