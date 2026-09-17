package com.aavarvd.halyra.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aavarvd.halyra.AppState
import com.aavarvd.halyra.AppState.BottomPanelTab
import java.awt.Cursor

@Composable
fun Terminal(
    appState: AppState,
    modifier: Modifier = Modifier
) {
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
                BottomPanelTab.OUTPUT -> OutputConsolePanel(appState)
                BottomPanelTab.TERMINAL -> key(BottomPanelTab.TERMINAL) {
                    ShellTerminalPanel(appState)
                }
            }
        }
    }
}

@Composable
private fun OutputConsolePanel(appState: AppState) {
    val scrollState = rememberScrollState()

    LaunchedEffect(appState.output) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Text(
        text = appState.output,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        color = AppColors.TerminalText,
        fontFamily = AppFonts.JBMono
    )
}


@Composable
private fun ShellTerminalPanel(appState: AppState) {
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    val shellOutput = appState.shellOutput
    val newlineIndex = shellOutput.lastIndexOf('\n')
    val scrollbackText = if (newlineIndex >= 0) shellOutput.substring(0, newlineIndex + 1) else ""
    val promptText = if (newlineIndex >= 0) shellOutput.substring(newlineIndex + 1) else shellOutput

    val terminalTextStyle = LocalTextStyle.current.copy(
        color = AppColors.TerminalText,
        fontFamily = AppFonts.JBMono
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(shellOutput, appState.shellInput) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        if (scrollbackText.isNotEmpty()) {
            Text(
                text = scrollbackText,
                modifier = Modifier.fillMaxWidth(),
                style = terminalTextStyle
            )
        }

        // Prompt + editable input on the same line, as the last line of the flow.
        Row(modifier = Modifier.fillMaxWidth()) {
            if (promptText.isNotEmpty()) {
                Text(
                    text = promptText,
                    style = terminalTextStyle
                )
            }

            BasicTextField(
                value = appState.shellInput,
                onValueChange = { appState.shellInput = it },
                modifier = Modifier
                    .weight(1f, fill = false)
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                            appState.shellProcess?.sendInput(appState.shellInput)
                            appState.shellInput = ""
                            true
                        } else {
                            false
                        }
                    },
                textStyle = terminalTextStyle,
                cursorBrush = SolidColor(AppColors.Cursor),
                singleLine = true
            )
        }
    }
}