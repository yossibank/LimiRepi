package jp.co.yahoo.yossibank.limirepi.ui.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformTabScaffold(
    modifier: Modifier,
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    content: @Composable (TabItem) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content(selectedTab)
    }
}
