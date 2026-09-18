package com.aavarvd.halyra.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aavarvd.halyra.AppState
import com.aavarvd.halyra.io.FileManager
import java.io.File
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

data class FileItem(val file: File, val level: Int)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FileTree(
    root: File,
    appState: AppState,
    fileManager: FileManager,
    modifier: Modifier = Modifier
) {
    val pythonIcon = remember { loadIcon("python.png") }
    val folderIcon = remember { loadIcon("folder.png") }
    val fileIcon = remember { loadIcon("file.png") }
    val textIcon = remember { loadIcon("text_dark.png") }
    val gitignoreIcon = remember { loadIcon("gitignore.png") }
    val xmlIcon = remember { loadIcon("xml_dark.png") }
    val jsonIcon = remember { loadIcon("json.png") }
    val mdIcon = remember { loadIcon("markdown.png") }

    var expandedPaths by remember { mutableStateOf(setOf(root.absolutePath)) }

    val visibleItems = remember(root, expandedPaths, appState.projectTreeRefreshTrigger) {
        val list = mutableListOf<FileItem>()
        fun addChildren(file: File, level: Int) {
            list.add(FileItem(file, level))
            if (file.isDirectory && expandedPaths.contains(file.absolutePath)) {
                val children = file.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
                children?.forEach { addChildren(it, level + 1) }
            }
        }
        addChildren(root, 0)
        list
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(Color(0xFF2B2B2B))
            .padding(top = 8.dp)
    ) {
        Text(
            text = "PROJECT",
            color = Color(0xFFBBBBBB),
            fontSize = 11.sp,
            fontFamily = AppFonts.Inter,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(visibleItems) { item ->
                FileNode(
                    item = item,
                    appState = appState,
                    fileManager = fileManager,
                    isExpanded = expandedPaths.contains(item.file.absolutePath),
                    onToggleExpand = {
                        expandedPaths = if (expandedPaths.contains(item.file.absolutePath)) {
                            expandedPaths - item.file.absolutePath
                        } else {
                            expandedPaths + item.file.absolutePath
                        }
                    },
                    pythonIcon = pythonIcon,
                    folderIcon = folderIcon,
                    fileIcon = fileIcon,
                    gitignoreIcon = gitignoreIcon,
                    xmlIcon = xmlIcon,
                    textIcon = textIcon,
                    jsonIcon = jsonIcon,
                    mdIcon = mdIcon,
                    onMultiSelect = { shift, ctrl ->
                        handleSelection(item.file, visibleItems, appState, shift, ctrl)
                    }
                )
            }
        }
    }
}

private fun handleSelection(
    clickedFile: File,
    visibleItems: List<FileItem>,
    appState: AppState,
    shift: Boolean,
    ctrl: Boolean
) {
    if (shift && appState.selectedFiles.isNotEmpty()) {
        val lastSelected = appState.selectedFiles.last()
        val startIndex = visibleItems.indexOfFirst { it.file == lastSelected }
        val endIndex = visibleItems.indexOfFirst { it.file == clickedFile }
        if (startIndex != -1 && endIndex != -1) {
            val range = if (startIndex < endIndex) startIndex..endIndex else endIndex..startIndex
            val toSelect = visibleItems.slice(range).map { it.file }
            toSelect.forEach { if (!appState.selectedFiles.contains(it)) appState.selectedFiles.add(it) }
        }
    } else if (ctrl) {
        if (appState.selectedFiles.contains(clickedFile)) {
            appState.selectedFiles.remove(clickedFile)
        } else {
            appState.selectedFiles.add(clickedFile)
        }
    } else {
        appState.selectedFiles.clear()
        appState.selectedFiles.add(clickedFile)
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun FileNode(
    item: FileItem,
    appState: AppState,
    fileManager: FileManager,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    pythonIcon: ImageBitmap,
    folderIcon: ImageBitmap,
    fileIcon: ImageBitmap,
    gitignoreIcon: ImageBitmap,
    xmlIcon: ImageBitmap,
    textIcon: ImageBitmap,
    jsonIcon: ImageBitmap,
    mdIcon: ImageBitmap,
    onMultiSelect: (Boolean, Boolean) -> Unit
) {
    val file = item.file
    val isDirectory = file.isDirectory
    var showContextMenu by remember { mutableStateOf(false) }
    val isSelected = appState.selectedFiles.contains(file)

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSelected) Color(0xFF2D5E8C) else Color.Transparent)
                .onPointerEvent(PointerEventType.Press) {
                    val isRightClick = it.button == PointerButton.Secondary
                    if (isRightClick) {
                        if (!isSelected) {
                            onMultiSelect(false, false)
                        }
                        showContextMenu = true
                    } else {
                        val shift = it.keyboardModifiers.isShiftPressed
                        val ctrl = it.keyboardModifiers.isCtrlPressed || it.keyboardModifiers.isMetaPressed
                        onMultiSelect(shift, ctrl)
                    }
                }
                .combinedClickable(
                    onClick = {
                        if (isDirectory) {
                            onToggleExpand()
                        }
                    },
                    onDoubleClick = {
                        if (!isDirectory) {
                            fileManager.openFile(file)
                        }
                    }
                )
                .padding(start = (item.level * 12).dp, top = 2.dp, bottom = 2.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isDirectory) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFAFB1B3),
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }

            val icon = when {
                isDirectory -> folderIcon
                file.name.endsWith(".py") -> pythonIcon
                file.name.endsWith(".txt") -> textIcon
                file.name.endsWith(".gitignore") -> gitignoreIcon
                file.name.endsWith(".xml") -> xmlIcon
                file.name.endsWith(".md") -> mdIcon
                file.name.endsWith(".json") -> jsonIcon
                else -> fileIcon
            }

            Image(
                bitmap = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp).padding(end = 4.dp)
            )

            Text(
                text = file.name,
                color = if (isSelected) Color.White else Color(0xFFBBBBBB),
                fontSize = 13.sp,
                fontFamily = AppFonts.Inter,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        CursorDropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false }
        ) {
            DropdownMenuItem(onClick = {
                showContextMenu = false
                if (isDirectory) onToggleExpand() else fileManager.openFile(file)
            }) {
                Text("Open")
            }
            Divider(color = Color(0xFF4B4B4B))
            DropdownMenuItem(onClick = {
                showContextMenu = false
                appState.newFileParent = if (isDirectory) file else file.parentFile
                appState.showNewFileDialog = true
            }) {
                Text("New File")
            }
            Divider(color = Color(0xFF4B4B4B))
            DropdownMenuItem(onClick = {
                showContextMenu = false
                fileManager.copyFiles(appState.selectedFiles.toList(), cut = true)
            }) {
                Text("Cut")
            }
            DropdownMenuItem(onClick = {
                showContextMenu = false
                fileManager.copyFiles(appState.selectedFiles.toList(), cut = false)
            }) {
                Text("Copy")
            }
            DropdownMenuItem(
                enabled = appState.clipboardFiles.isNotEmpty(),
                onClick = {
                    showContextMenu = false
                    fileManager.pasteFiles(if (isDirectory) file else file.parentFile!!)
                }
            ) {
                Text("Paste")
            }
            Divider(color = Color(0xFF4B4B4B))
            DropdownMenuItem(onClick = {
                showContextMenu = false
                fileManager.deleteFiles(appState.selectedFiles.toList())
                appState.selectedFiles.clear()
            }) {
                Text("Delete")
            }
        }
    }
}
