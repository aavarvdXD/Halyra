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
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.weisj.jsvg.parser.SVGLoader
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.ceil

private fun loadIcon(name: String): ImageBitmap {
    val url = Thread.currentThread()
        .contextClassLoader
        .getResource("images/$name")
        ?: error("Could not find icon: images/$name")

    val loader = SVGLoader()
    val document = loader.load(url)
        ?: error("Could not parse SVG: $name")

    val svgSize = document.size()

    val width = ceil(svgSize.width.toDouble()).toInt().coerceAtLeast(1)
    val height = ceil(svgSize.height.toDouble()).toInt().coerceAtLeast(1)

    val image = BufferedImage(
        width,
        height,
        BufferedImage.TYPE_INT_ARGB
    )

    val graphics = image.createGraphics()

    graphics.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
    )

    graphics.setRenderingHint(
        RenderingHints.KEY_STROKE_CONTROL,
        RenderingHints.VALUE_STROKE_PURE
    )

    document.render(null, graphics)

    graphics.dispose()

    return image.toComposeImageBitmap()
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
    onStop: () -> Unit,
    isRunning: Boolean,
    onSave: () -> Unit,
    onShowOutput: () -> Unit,
    onShowTerminal: () -> Unit,
    onToggleProjectTree: () -> Unit,
    onSetting: () -> Unit,
    onOpenFolder: () -> Unit,
) {
    val newIcon = remember { loadIcon("new.svg") }
    val openIcon = remember { loadIcon("open.svg") }
    val openFolderIcon = remember { loadIcon("folder.svg") }
    val runIcon = remember { loadIcon("run.svg") }
    val stopIcon = remember { loadIcon("stop.svg") }
    val saveIcon = remember { loadIcon("save.svg") }
    val runConsoleIcon = remember { loadIcon("runConsole.svg") }
    val shellIcon = remember { loadIcon("shell.svg") }
    val projectIcon = remember { loadIcon("projectStructure.svg") }
    val settingsIcon = remember { loadIcon("settings.svg") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(56.dp)
            .background(Color(0xFF1E1E1E))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SidebarButton(icon = newIcon, hint = "New File", onClick = onNew)
        Spacer(modifier = Modifier.height(4.dp))
        SidebarButton(icon = openIcon, hint = "Open File", onClick = onOpen)
        Spacer(modifier = Modifier.height(4.dp))
        SidebarButton(icon = openFolderIcon, hint = "Open Folder", onClick = onOpenFolder)
        Spacer(modifier = Modifier.height(4.dp))
        SidebarButton(icon = saveIcon, hint = "Save File", onClick = onSave)

        if (isRunning)
            SidebarButton(icon = stopIcon, hint = "Stop", onClick = onStop)
        else
            SidebarButton(icon = runIcon, hint = "Run Python", onClick = onRun)

        Spacer(modifier = Modifier.weight(1f))

        SidebarButton(icon = projectIcon, hint = "Project Structure", onClick = onToggleProjectTree)
        Spacer(modifier = Modifier.height(4.dp))
        SidebarButton(icon = settingsIcon, hint = "Settings", onClick = onSetting)
        Spacer(modifier = Modifier.height(4.dp))
        SidebarButton(icon = runConsoleIcon, hint = "Output Console", onClick = onShowOutput)
        Spacer(modifier = Modifier.height(4.dp))
        SidebarButton(icon = shellIcon, hint = "Terminal", onClick = onShowTerminal)
    }
}