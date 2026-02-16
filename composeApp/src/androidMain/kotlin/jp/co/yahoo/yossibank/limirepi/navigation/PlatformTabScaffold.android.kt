package jp.co.yahoo.yossibank.limirepi.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformTabScaffold(
    modifier: Modifier,
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    content: @Composable (TabItem) -> Unit
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                TabItem.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(selectedTab)
    }
}
