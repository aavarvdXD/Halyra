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
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aavarvd.halyra.editor.EditorTab
import java.io.InputStream

private fun loadTabIcon(name: String): ImageBitmap {
    val stream: InputStream =
        Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("images/$name")
            ?: error("Could not find icon: images/$name")

    return stream.use {
        loadImageBitmap(it)
    }
}

@Composable
fun TabBar(
    tabs: List<EditorTab>,
    activeTabIndex: Int,
    onTabClick: (Int) -> Unit,
    onTabClose: (Int) -> Unit
) {
    val pythonIcon = remember { loadTabIcon("python.png") }
    val fileIcon = remember { loadTabIcon("file.png") }
    val textIcon = remember { loadTabIcon("text_dark.png") }
    val gitignoreIcon = remember { loadTabIcon("gitignore.png") }
    val xmlIcon = remember { loadTabIcon("xml_dark.png") }
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
