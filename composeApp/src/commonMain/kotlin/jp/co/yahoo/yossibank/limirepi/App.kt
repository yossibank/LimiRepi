package jp.co.yahoo.yossibank.limirepi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.ui.FridgeScreen
import jp.co.yahoo.yossibank.limirepi.feature.receipt.ui.RegisterScreen
import jp.co.yahoo.yossibank.limirepi.feature.recipeGenerate.RecipeGenerateScreen
import jp.co.yahoo.yossibank.limirepi.feature.recipeList.RecipeListScreen
import jp.co.yahoo.yossibank.limirepi.feature.setting.SettingsScreen
import jp.co.yahoo.yossibank.limirepi.ui.tab.PlatformTabScaffold
import jp.co.yahoo.yossibank.limirepi.ui.tab.TabItem

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
        TabItem.FRIDGE -> FridgeScreen(modifier = modifier)
        TabItem.REGISTER -> RegisterScreen(modifier)
        TabItem.RECIPE_GENERATE -> RecipeGenerateScreen(modifier)
        TabItem.RECIPE_LIST -> RecipeListScreen(modifier)
        TabItem.SETTINGS -> SettingsScreen(modifier)
    }
}