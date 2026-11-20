package com.aavarvd.halyra.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
import com.aavarvd.halyra.ui.AppColors
import com.aavarvd.halyra.editor.completion.CompletionEngine
import com.aavarvd.halyra.editor.completion.CompletionPopup
import com.aavarvd.halyra.editor.completion.CompletionState

@Composable
fun EditorPane(
    activeTab: EditorTab,
    editorState: EditorState,
    inputHandler: EditorInputHandler,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val selectionColors = remember {
        TextSelectionColors(
            handleColor = AppColors.SelectionHandle,
            backgroundColor = AppColors.SelectionBackground
        )
    }

    val isPythonFile = activeTab.title.endsWith(".py") == true
    val pythonTransformation = remember { PythonHighLightTransformation() }
    val completionEngine = remember { CompletionEngine() }
    val completionState = remember { CompletionState() }

    val cursor = activeTab.text.selection.start
    val textLayout = editorState.textLayout

    val caretRect = textLayout?.takeIf {
        cursor in 0..it.layoutInput.text.length
    }?.getCursorRect(cursor)
    val density = LocalDensity.current

    val popupOffset = caretRect?.let { rect ->
        with(density) {
            IntOffset(
                x = (4.dp.toPx() + rect.left).roundToInt(),
                y = (
                    8.dp.toPx() +
                    rect.bottom -
                    editorState.scrollState.value
                ).roundToInt()
            )
        }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .background(AppColors.Background)
        ) {
            BasicTextField(
                value = activeTab.text,
                onValueChange = { newValue ->
                    inputHandler.onTextChange(newValue)

                    val suggestions = completionEngine.complete(
                        newValue.text,
                        newValue.selection.start
                    )

                    completionState.update(suggestions)
                },
                modifier = modifier
                    .fillMaxHeight()
                    .background(AppColors.Background)
                    .onSizeChanged { editorState.viewportHeightPx = it.height }
                    .verticalScroll(editorState.scrollState)
                    .padding(start = 4.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                    .onPreviewKeyEvent { inputHandler.handleKeyEvent(it) },
                textStyle = textStyle,
                cursorBrush = SolidColor(AppColors.Cursor),
                visualTransformation = if (isPythonFile) pythonTransformation else VisualTransformation.None,
                onTextLayout = { editorState.textLayout = it },
                singleLine = false
            )

            if (
                isPythonFile &&
                completionState.isVisible &&
                popupOffset != null
            ) {
                CompletionPopup(
                    items = completionState.suggestions,
                    selectedIndex = 0,
                    onSelect = { item ->
                        //TODO: Aceptance
                    },
                    modifier = Modifier.offset { popupOffset }
                )
            }
        }
    }
}