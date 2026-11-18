package com.aavarvd.halyra.editor.completion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CompletionState() {
    var suggestions by mutableStateOf<List<CompletionItem>>(emptyList())
        private set

    var isVisible by mutableStateOf(false)
        private set

    fun update(items: List<CompletionItem>) {
        suggestions = items
        isVisible = items.isNotEmpty()
    }

    fun hide() {
        suggestions = emptyList()
        isVisible = false
    }
}