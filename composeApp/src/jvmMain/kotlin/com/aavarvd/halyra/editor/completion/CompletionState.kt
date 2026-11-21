package com.aavarvd.halyra.editor.completion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CompletionState() {
    var suggestions by mutableStateOf<List<CompletionItem>>(emptyList())
        private set

    var selectedIndex by mutableStateOf(0)
        private set

    var isVisible: Boolean = false
        get() = suggestions.isNotEmpty()

    val selectedItem: CompletionItem?
        get() = suggestions.getOrNull(selectedIndex)

    fun update(items: List<CompletionItem>) {
        suggestions = items
        selectedIndex = 0
    }

    fun moveSelection(direction: Int) {
        if (suggestions.isEmpty()) return

        selectedIndex = (selectedIndex + direction)
            .mod(suggestions.size)
    }

    fun hide() {
        suggestions = emptyList()
        selectedIndex = 0
    }
}