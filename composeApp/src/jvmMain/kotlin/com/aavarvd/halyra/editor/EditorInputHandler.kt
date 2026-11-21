package com.aavarvd.halyra.editor

import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.aavarvd.halyra.AppState

class EditorInputHandler(private val appState: AppState) {

    fun onTextChange(newValue: TextFieldValue) {
        val activeTab = appState.activeTab ?: return
        val activeTabIndex = appState.activeTabIndex
        
        val oldText = activeTab.text.text
        val newText = newValue.text
        val newSelection = newValue.selection

        if (newText.length == oldText.length + 1 && newSelection.collapsed) {
            val cursor = newSelection.start
            val insertedChar = if (cursor > 0) newText[cursor - 1] else null

            val closingChar = when (insertedChar) {
                '(' -> ')'
                '[' -> ']'
                '{' -> '}'
                '"' -> '"'
                '\'' -> '\''
                else -> null
            }

            if (closingChar != null) {
                // Handle typing over auto-closed quotes
                if ((insertedChar == '"' || insertedChar == '\'') &&
                    cursor <= oldText.length &&
                    oldText.getOrNull(cursor - 1) == insertedChar
                ) {
                    appState.tabs[activeTabIndex] = activeTab.copy(
                        text = TextFieldValue(oldText, selection = TextRange(cursor))
                    )
                    return
                }

                val finalText = newText.substring(0, cursor) + closingChar + newText.substring(cursor)
                appState.tabs[activeTabIndex] = activeTab.copy(
                    text = TextFieldValue(finalText, selection = TextRange(cursor))
                )
                return
            }

            // Handle typing over auto-closed brackets
            if ((insertedChar == ')' || insertedChar == ']' || insertedChar == '}') &&
                cursor <= oldText.length &&
                oldText.getOrNull(cursor - 1) == insertedChar
            ) {
                appState.tabs[activeTabIndex] = activeTab.copy(
                    text = TextFieldValue(oldText, selection = TextRange(cursor))
                )
                return
            }
        }

        val insertedNewLine = newText.length == oldText.length + 1 &&
                newSelection.start > 0 &&
                newText[newSelection.start - 1] == '\n'

        if (insertedNewLine) {
            val cursor = newSelection.start
            val beforeCursor = newText.substring(0, cursor)
            val lines = beforeCursor.split('\n')

            if (lines.size >= 2) {
                val previousLine = lines[lines.size - 2]
                val baseIndent = previousLine.takeWhile { it == ' ' || it == '\t' }
                val extraIndent = if (previousLine.trimEnd().endsWith(":")) "    " else ""
                val indent = baseIndent + extraIndent

                val finalText = newText.substring(0, cursor) + indent + newText.substring(cursor)
                appState.tabs[activeTabIndex] = activeTab.copy(
                    text = TextFieldValue(finalText, selection = TextRange(cursor + indent.length))
                )
                return
            }
        }

        appState.tabs[activeTabIndex] = activeTab.copy(text = newValue)
    }

    fun handleKeyEvent(event: KeyEvent): Boolean {
        val activeTab = appState.activeTab ?: return false
        val activeTabIndex = appState.activeTabIndex

        if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
            val selectionStart = minOf(activeTab.text.selection.start, activeTab.text.selection.end)
            val selectionEnd = maxOf(activeTab.text.selection.start, activeTab.text.selection.end)

            if (selectionStart != selectionEnd) {
                appState.tabs[activeTabIndex] = activeTab.copy(
                    text = TextFieldValue(
                        activeTab.text.text.removeRange(selectionStart, selectionEnd),
                        TextRange(selectionStart)
                    )
                )
            } else if (selectionStart > 0) {
                val lastChunk = activeTab.text.text.substring(0, selectionStart).takeLast(4)
                val deleteCount = if (lastChunk.length == 4 && lastChunk.all { it == ' ' }) 4 else 1

                appState.tabs[activeTabIndex] = activeTab.copy(
                    text = TextFieldValue(
                        activeTab.text.text.removeRange(selectionStart - deleteCount, selectionStart),
                        TextRange(selectionStart - deleteCount)
                    )
                )
            }
            return true
        }
        return false
    }

    fun acceptCompletion(insertText: String) {
        val activeTab = appState.activeTab ?: return
        val activeTabIndex = appState.activeTabIndex

        val text = activeTab.text.text
        val cursor = activeTab.text.selection.start

        if (!activeTab.text.selection.collapsed) return

        val prefixStart = text.substring(0, cursor)
            .indexOfLast { !it.isLetterOrDigit() && it != '_' } + 1

        val finalText = text.substring(0, prefixStart) +
            insertText +
            text.substring(cursor)

        val newCursor = prefixStart + insertText.length

        appState.tabs[activeTabIndex] = activeTab.copy(
            text = TextFieldValue(
                finalText,
                selection = TextRange(newCursor)
            )
        )
    }
}
