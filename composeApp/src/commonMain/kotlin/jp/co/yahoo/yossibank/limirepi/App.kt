package jp.co.yahoo.yossibank.limirepi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import jp.co.yahoo.yossibank.limirepi.navigation.PlatformTabScaffold
import jp.co.yahoo.yossibank.limirepi.navigation.TabItem
import jp.co.yahoo.yossibank.limirepi.screen.FridgeScreen
import jp.co.yahoo.yossibank.limirepi.screen.RecipeGenerateScreen
import jp.co.yahoo.yossibank.limirepi.screen.RecipeListScreen
import jp.co.yahoo.yossibank.limirepi.screen.RegisterScreen
import jp.co.yahoo.yossibank.limirepi.screen.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var selectedTab by remember { mutableStateOf(TabItem.FRIDGE) }

    MaterialTheme {
        PlatformTabScaffold(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        ) { tab ->
            TabContent(tab = tab)
        }
    }
}

@Composable
fun TabContent(
    tab: TabItem,
    modifier: Modifier = Modifier
) {
    when (tab) {
        TabItem.FRIDGE -> FridgeScreen(modifier)
        TabItem.REGISTER -> RegisterScreen(modifier)
        TabItem.RECIPE_GENERATE -> RecipeGenerateScreen(modifier)
        TabItem.RECIPE_LIST -> RecipeListScreen(modifier)
        TabItem.SETTINGS -> SettingsScreen(modifier)
    }
}