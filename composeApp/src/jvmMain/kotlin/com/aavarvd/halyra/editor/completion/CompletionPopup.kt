package com.aavarvd.halyra.editor.completion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PopupBackground = Color(0xFF303030)
private val PopupBorder = Color(0xFF454545)
private val SelectedBackground = Color(0xFF3D4652)
private val MutedText = Color(0xFF858585)

@Composable
fun CompletionPopup(
    items: List<CompletionItem>,
    selectedIndex: Int = 0,
    onSelect: (CompletionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(
        modifier = modifier
            .widthIn(min = 260.dp, max = 320.dp)
            .heightIn(max = 280.dp)
            .shadow(12.dp, RoundedCornerShape(8.dp))
            .background(PopupBackground)
            .border(1.dp, PopupBorder, RoundedCornerShape(8.dp))
            .padding(5.dp)
    ) {
        items.take(8).forEachIndexed { index, item ->
            val selected = selectedIndex == index

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        if (selected) SelectedBackground
                        else Color.Transparent
                    )
                    .clickable { onSelect(item) }
                    .padding(horizontal = 9.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompletionKindIcon(item.kind)

                Text(
                    text = item.label,
                    color = Color(0xFFE2E2E2),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold
                    else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = item.kind.name.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    color = MutedText,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enter",
                color = Color(0xFFAAAAAA),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.width(5.dp))

            Text(
                text = "to insert suggestion",
                color = MutedText,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun CompletionKindIcon(kind: CompletionKind) {
    val (symbol, color) = when (kind) {
        CompletionKind.KEYWORD -> "K" to Color(0xFFC586C0)
        CompletionKind.BUILTIN -> "ƒ" to Color(0xFF80C6E8)
        CompletionKind.VARIABLE -> "x" to Color(0xFF9CDCFE)
        CompletionKind.FUNCTION -> "ƒ" to Color(0xFFDCDCAA)
        CompletionKind.CLASS -> "C" to Color(0xFF4EC9B0)
        CompletionKind.ATTRIBUTE -> "·" to Color(0xFFCE9178)
    }

    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.13f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}