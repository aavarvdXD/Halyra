package com.aavarvd.halyra.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.foundation.hoverable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.WindowPlacement
import com.aavarvd.halyra.usableBounds
import java.awt.Frame

import androidx.compose.material.Text

import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowScope
import kotlin.system.exitProcess

@Composable
fun WindowScope.AppTitleBar(
    windowState: WindowState,
    onClose: () -> Unit
) {
    val isMaximized = windowState.placement == WindowPlacement.Maximized

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Color(0xFF2B2B2B))
    ) {
        WindowDraggableArea {
            Box(modifier = Modifier.fillMaxSize())
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Halyra",
                color = Color(0xFF56b5c3),
                fontFamily = AppFonts.Inter,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TitleBarButton("─") {
                    windowState.isMinimized = true
                }

                TitleBarButton(
                    if (windowState.placement == WindowPlacement.Maximized) "❐" else "□"
                ) {
                    windowState.placement =
                        if (windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized
                }

                TitleBarButton("✕") {
                    onClose()
                }
            }
        }
    }
}

@Composable
fun TitleBarButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .width(46.dp)
            .fillMaxHeight()
            .hoverable(interactionSource)
            .clickable { onClick() }
            .background(
                when {
                    hovered && text == "✕" -> Color(0xFFB22222)
                    hovered -> Color(0xFF515151)
                    else -> Color(0xFF2B2B2B)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontFamily = AppFonts.Inter
        )
    }
}
