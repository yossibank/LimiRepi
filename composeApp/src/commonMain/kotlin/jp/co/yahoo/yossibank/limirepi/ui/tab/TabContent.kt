package jp.co.yahoo.yossibank.limirepi.ui.tab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.ui.FridgeScreen
import jp.co.yahoo.yossibank.limirepi.feature.receipt.ui.RegisterScreen
import jp.co.yahoo.yossibank.limirepi.feature.recipeGenerate.RecipeGenerateScreen
import jp.co.yahoo.yossibank.limirepi.feature.recipeList.RecipeListScreen
import jp.co.yahoo.yossibank.limirepi.feature.setting.SettingsScreen

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