package com.aavarvd.halyra.editor.completion

enum class CompletionKind {
    KEYWORD,
    BUILTIN,
    VARIABLE,
    FUNCTION,
    CLASS,
    ATTRIBUTE,
}

data class CompletionItem(
    val label: String,
    val insertText: String,
    val kind: CompletionKind,
)