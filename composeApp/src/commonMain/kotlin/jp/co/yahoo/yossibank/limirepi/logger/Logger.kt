package jp.co.yahoo.yossibank.limirepi.logger

interface Logger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warning(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

expect fun createLogger(): Logger

object AppLogger {
    private val logger: Logger = createLogger()

    fun d(tag: String, message: String) {
        logger.debug(tag, message)
    }

    fun i(tag: String, message: String) {
        logger.info(tag, message)
    }

    fun w(tag: String, message: String) {
        logger.warning(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        logger.error(tag, message, throwable)
    }
}
