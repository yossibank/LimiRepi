package jp.co.yahoo.yossibank.limirepi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TabItem(
    val position: Int,
    val title: String,
    val icon: ImageVector
) {
    FRIDGE(0, "冷蔵庫", Icons.Default.Kitchen),
    REGISTER(1, "食材登録", Icons.Default.AddCircle),
    RECIPE_GENERATE(2, "レシピ生成", Icons.Default.AutoAwesome),
    RECIPE_LIST(3, "レシピ一覧", Icons.AutoMirrored.Filled.MenuBook),
    SETTINGS(4, "設定", Icons.Default.Settings);

    companion object {
        fun fromPosition(position: Int): TabItem =
            entries.first { it.position == position }
    }
}
