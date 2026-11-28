package com.aavarvd.halyra.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SettingsState {
    var selectedCategory by mutableStateOf(
        SettingsCategory.INTERPRETER
    )
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun selectCategory(category: SettingsCategory) {
        selectedCategory = category
    }

    fun updateSearch(query: String) {
        searchQuery = query
    }
}