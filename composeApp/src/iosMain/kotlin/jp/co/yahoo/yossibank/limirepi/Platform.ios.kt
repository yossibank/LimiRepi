package jp.co.yahoo.yossibank.limirepi

import cocoapods.LoremIpsum.LoremIpsum
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    @OptIn(ExperimentalForeignApi::class)
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion + LoremIpsum.name()
}

actual fun getPlatform(): Platform = IOSPlatform()