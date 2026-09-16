package com.aavarvd.halyra.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.aavarvd.halyra.ui.AppFonts
import java.io.InputStream

private fun loadIcon(name: String): ImageBitmap {
    val stream: InputStream =
        Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("images/$name")
            ?: error("Could not find icon: images/$name")

    return stream.use {
        loadImageBitmap(it)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun SidebarButton(
    icon: ImageBitmap,
    hint: String,
    onClick: () -> Unit
) {
    var hovered by remember { mutableStateOf(false) }

    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.padding(8.dp),
                color = Color(0xFF323232),
                shape = RoundedCornerShape(4.dp),
                elevation = 4.dp
            ) {
                Text(
                    text = hint,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White,
                    fontFamily = AppFonts.JBMono
                )
            }
        },
        delayMillis = 500,
        tooltipPlacement = TooltipPlacement.CursorPoint(
            alignment = Alignment.BottomEnd,
            offset = DpOffset(x = 10.dp, y = 10.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .onPointerEvent(PointerEventType.Enter) { hovered = true }
                .onPointerEvent(PointerEventType.Exit) { hovered = false }
                .clickable(
                    onClick = onClick
                )
                .background(
                    color = if (hovered) {
                        Color(0xFF4C5052)
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = icon,
                contentDescription = hint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SidebarSeparator() {
    Box(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .width(32.dp)
            .height(1.dp)
            .background(Color(0xFF3A3A3A))
    )
}

@Composable
fun Sidebar(
    onNew: () -> Unit,
    onOpen: () -> Unit,
    onRun: () -> Unit,
    onSave: () -> Unit,
    onShowOutput: () -> Unit,
    onShowTerminal: () -> Unit,
    onToggleProjectTree: () -> Unit
) {
    val newIcon = remember { loadIcon("new.png") }
    val openIcon = remember { loadIcon("open.png") }
    val runIcon = remember { loadIcon("run.png") }
    val saveIcon = remember { loadIcon("save.png") }
    val runConsoleIcon = remember { loadIcon("runConsole.png") }
    val shellIcon = remember { loadIcon("shell.png") }
    val folderIcon = remember { loadIcon("folder.png") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(56.dp)
            .background(Color(0xFF1E1E1E))
            .padding(
                vertical = 6.dp,
                horizontal = 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SidebarButton(
            icon = newIcon,
            hint = "New File",
            onClick = onNew
        )

        Spacer(modifier = Modifier.height(4.dp))

        SidebarButton(
            icon = openIcon,
            hint = "Open File",
            onClick = onOpen
        )

        Spacer(modifier = Modifier.height(4.dp))

        SidebarButton(
            icon = runIcon,
            hint = "Run Python",
            onClick = onRun
        )

        Spacer(modifier = Modifier.height(4.dp))

        SidebarButton(
            icon = saveIcon,
            hint = "Save File",
            onClick = onSave
        )

        SidebarSeparator()

        SidebarButton(
            icon = runConsoleIcon,
            hint = "Output Console",
            onClick = onShowOutput
        )

        Spacer(modifier = Modifier.height(4.dp))

        SidebarButton(
            icon = shellIcon,
            hint = "Terminal",
            onClick = onShowTerminal
        )

        SidebarSeparator()

        SidebarButton(
            icon = folderIcon,
            hint = "Project Structure",
            onClick = onToggleProjectTree
        )
    }
}