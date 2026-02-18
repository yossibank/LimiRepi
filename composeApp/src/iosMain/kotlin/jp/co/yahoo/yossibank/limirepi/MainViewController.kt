package jp.co.yahoo.yossibank.limirepi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import jp.co.yahoo.yossibank.limirepi.ui.tab.TabContent
import jp.co.yahoo.yossibank.limirepi.ui.tab.TabItem

fun TabContentViewController(tabPosition: Int) = ComposeUIViewController {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val tab = TabItem.fromPosition(tabPosition)
            TabContent(tab = tab)
        }
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