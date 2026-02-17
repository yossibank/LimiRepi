package jp.co.yahoo.yossibank.limirepi.ui.tab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformTabScaffold(
    modifier: Modifier = Modifier,
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    content: @Composable (TabItem) -> Unit
)
