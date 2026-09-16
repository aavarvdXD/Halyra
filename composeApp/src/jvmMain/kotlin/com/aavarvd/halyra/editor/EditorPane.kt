package com.aavarvd.halyra.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aavarvd.halyra.ui.AppColors

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

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        BasicTextField(
            value = activeTab.text,
            onValueChange = inputHandler::onTextChange,
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
    }
}