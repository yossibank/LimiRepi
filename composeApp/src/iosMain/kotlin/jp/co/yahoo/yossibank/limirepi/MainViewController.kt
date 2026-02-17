package jp.co.yahoo.yossibank.limirepi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import jp.co.yahoo.yossibank.limirepi.ui.tab.TabItem

// SwiftUI TabView用に各タブのComposeコンテンツを個別に提供する
fun TabContentViewController(tabPosition: Int) = ComposeUIViewController {
    var selectedTab by remember { mutableStateOf(TabItem.fromPosition(tabPosition)) }

    MaterialTheme {
        App()
    }
}

fun getTabItems(): List<TabItemInfo> = TabItem.entries.map { tab ->
    TabItemInfo(
        position = tab.position,
        title = tab.title,
        iconName = when (tab) {
            TabItem.FRIDGE -> "refrigerator"
            TabItem.REGISTER -> "plus.circle"
            TabItem.RECIPE_GENERATE -> "wand.and.stars"
            TabItem.RECIPE_LIST -> "book"
            TabItem.SETTINGS -> "gearshape"
        }
    )
}

data class TabItemInfo(
    val position: Int,
    val title: String,
    val iconName: String
)