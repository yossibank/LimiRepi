package jp.co.yahoo.yossibank.limirepi.feature.fridge.ui.card

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeCategory

data class FridgeItemCategoryHeaderState(
    val category: FridgeCategory,
    val itemCount: Int,
    val isCollapsed: Boolean
)

@Composable
fun FridgeItemCategoryHeader(
    state: FridgeItemCategoryHeaderState,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (state.isCollapsed) 0f else 90f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotationAngle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        state.category.color,
                        state.category.color.copy(alpha = 0.75f)
                    )
                )
            )
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.category.emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.category.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${state.itemCount}件の食材",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.25f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (state.isCollapsed) "展開" else "折り畳み",
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(rotationAngle)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun FridgeItemCategoryHeaderPreview() {
    MaterialTheme {
        Column {
            FridgeItemCategoryHeader(
                state = FridgeItemCategoryHeaderState(
                    category = FridgeCategory.MEAT,
                    itemCount = 3,
                    isCollapsed = false
                ),
                onToggle = {}
            )
            FridgeItemCategoryHeader(
                state = FridgeItemCategoryHeaderState(
                    category = FridgeCategory.LEAFY_VEGETABLE,
                    itemCount = 5,
                    isCollapsed = true
                ),
                onToggle = {}
            )
            FridgeItemCategoryHeader(
                state = FridgeItemCategoryHeaderState(
                    category = FridgeCategory.FISH,
                    itemCount = 2,
                    isCollapsed = false
                ),
                onToggle = {}
            )
        }
    }
}
