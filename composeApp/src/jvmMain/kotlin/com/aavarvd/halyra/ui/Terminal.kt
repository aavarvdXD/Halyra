package com.aavarvd.halyra.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import com.aavarvd.halyra.AppState
import com.aavarvd.halyra.AppState.BottomPanelTab
import java.awt.Cursor

@Composable
fun Terminal(
    appState: AppState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val minHeightPx = with(density) { 60.dp.toPx() }
    val maxHeightPx = with(density) { 420.dp.toPx() }

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(AppColors.Divider)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        appState.terminalHeightPx = (appState.terminalHeightPx - dragAmount.y)
                            .coerceIn(minHeightPx, maxHeightPx)
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { appState.terminalHeightPx.toInt().toDp() })
                .background(AppColors.TerminalBackground)
                .padding(8.dp)
        ) {
            when (appState.bottomPanelTab) {
                BottomPanelTab.OUTPUT -> OutputPanel(appState)
                BottomPanelTab.TERMINAL -> ShellPanel(appState, onRestart = {
                    appState.startShell(coroutineScope)
                })
            }
        }
    }
}

private fun loadIcon(name: String): ImageBitmap {
    val stream: java.io.InputStream =
        Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("images/$name")
            ?: error("Could not find icon: images/$name")

    return stream.use { loadImageBitmap(it) }
}

@Composable
private fun SmallIconButton(icon: ImageBitmap, hint: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(20.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(bitmap = icon, contentDescription = hint, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun OutputPanel(appState: AppState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = appState.output,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            color = AppColors.TerminalText,
            fontFamily = AppFonts.JBMono
        )

        BasicTextField(
            value = appState.terminalInput,
            onValueChange = { appState.terminalInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textStyle = LocalTextStyle.current.copy(
                color = AppColors.TerminalText,
                fontFamily = AppFonts.JBMono
            ),
            cursorBrush = SolidColor(AppColors.Cursor),
            singleLine = true
        )
    }
}

@Composable
private fun ShellPanel(appState: AppState, onRestart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            SmallIconButton(
                icon = remember { loadIcon("restart.png") },
                hint = "Restart Terminal",
                onClick = onRestart
            )
        }

        Text(
            text = appState.shellOutput,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            color = AppColors.TerminalText,
            fontFamily = AppFonts.JBMono
        )

        BasicTextField(
            value = appState.shellInput,
            onValueChange = { appState.shellInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                        appState.shellProcess?.sendInput(appState.shellInput)
                        appState.shellInput = ""
                        true
                    } else {
                        false
                    }
                },
            textStyle = LocalTextStyle.current.copy(
                color = AppColors.TerminalText,
                fontFamily = AppFonts.JBMono
            ),
            cursorBrush = SolidColor(AppColors.Cursor),
            singleLine = true
        )
    }
}