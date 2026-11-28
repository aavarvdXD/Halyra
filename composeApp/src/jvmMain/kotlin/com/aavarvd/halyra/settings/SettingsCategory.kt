package com.aavarvd.halyra.settings

enum class SettingsCategory(
    val title: String,
    val group: String
) {
    INTERPRETER("Python Interpreter", "Development"),
    PACKAGES("Packages", "Development"),
    EDITOR("Editor", "Development"),

    FONTS("Fonts", "Appearance"),
    THEME("Theme", "Appearance"),

    KEYMAP("Keymap", "Interface"),
    PLUGINS("Plugins", "Interface"),
    TERMINAL("Terminal", "Interface"),

    ADVANCED("Advanced", "System"),
}