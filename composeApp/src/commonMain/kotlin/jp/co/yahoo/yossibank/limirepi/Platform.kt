package jp.co.yahoo.yossibank.limirepi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform