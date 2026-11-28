package com.aavarvd.halyra.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aavarvd.halyra.editor.EditorTab
import com.github.weisj.jsvg.parser.SVGLoader
import org.jetbrains.compose.resources.imageResource
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.ceil

private fun loadTabIcon(name: String): ImageBitmap {
    val url = Thread.currentThread()
        .contextClassLoader
        .getResource("images/$name")
        ?: error("Could not parse SVG: $name")

    val document = SVGLoader().load(url)
        ?: error("Could not load SVG: $name")

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

@Composable
fun TabBar(
    tabs: List<EditorTab>,
    activeTabIndex: Int,
    onTabClick: (Int) -> Unit,
    onTabClose: (Int) -> Unit
) {
    val pythonIcon = remember { loadTabIcon("python.svg") }
    val fileIcon = remember { loadTabIcon("file.svg") }
    val textIcon = remember { loadTabIcon("text.svg") }
    val gitignoreIcon = remember { loadTabIcon("gitignore.svg") }
    val xmlIcon = remember { loadTabIcon("xml.svg") }
    val listState = rememberLazyListState()

    val tabsCount = tabs.size
    LaunchedEffect(activeTabIndex, tabsCount) {
        if (activeTabIndex in tabs.indices) {
            listState.animateScrollToItem(activeTabIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF252525))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                itemsIndexed(tabs) { index, tab ->
                    val isActive = index == activeTabIndex
                    val tabIcon = when {
                        tab.title.endsWith(".py") -> pythonIcon
                        tab.title.endsWith(".txt") -> textIcon
                        tab.title.endsWith(".gitignore") -> gitignoreIcon
                        tab.title.endsWith(".xml") -> xmlIcon
                        else -> fileIcon
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(min = 100.dp, max = 200.dp)
                            .background(if (isActive) Color(0xFF2B2B2B) else Color(0xFF252525))
                            .clickable { onTabClick(index) }
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = tabIcon,
                            contentDescription = "Tab Icon",
                            modifier = Modifier.size(20.dp).padding(end = 6.dp)
                        )

                        Text(
                            text = tab.title + (if (tab.isModified) "*" else ""),
                            color = if (isActive) Color.White else Color(0xFF888888),
                            fontSize = 12.sp,
                            maxLines = 1,
                            fontFamily = AppFonts.Inter,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Tab",
                            tint = if (isActive) Color.White else Color(0xFF888888),
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onTabClose(index) }
                        )
                    }
                    if (!isActive && index < tabs.size - 1 && index + 1 != activeTabIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .padding(vertical = 8.dp)
                                .background(Color(0xFF333333))
                        )
                    }
                }
            }
        }

        HorizontalScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
            adapter = rememberScrollbarAdapter(listState),
            style = androidx.compose.foundation.ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 2.dp,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(1.dp),
                hoverDurationMillis = 300,
                unhoverColor = Color(0xFF444444),
                hoverColor = Color(0xFF666666)
            )
        )
    }
}
