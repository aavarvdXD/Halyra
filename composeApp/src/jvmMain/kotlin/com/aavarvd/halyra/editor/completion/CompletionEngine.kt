package com.aavarvd.halyra.editor.completion

class CompletionEngine {
    private val keywords = setOf(
        "False", "None", "True",
        "and", "as", "assert", "async", "await",
        "break", "class", "continue",
        "def", "del",
        "elif", "else", "except",
        "finally", "for", "from",
        "global",
        "if", "import", "in", "is",
        "lambda",
        "nonlocal", "not",
        "or",
        "pass",
        "raise", "return",
        "try",
        "while", "with",
        "yield"
    )

    private val builtins = setOf(
        "print", "input",

        "bool", "int", "float", "complex",
        "str", "list", "tuple", "dict",
        "set", "frozenset", "bytes", "bytearray",

        "abs", "round", "pow", "divmod",
        "sum", "min", "max",

        "range", "enumerate", "zip",
        "map", "filter", "iter", "next",
        "reversed", "sorted",

        "type", "isinstance", "issubclass",
        "id", "hash", "callable", "hasattr",
        "getattr", "setattr", "delattr",
        "dir", "vars", "globals", "locals",

        "repr", "ascii", "format", "ord", "chr",

        "len", "all", "any",

        "open", "compile", "eval", "exec",

        "object", "super", "property",
        "classmethod", "staticmethod",

        "help", "breakpoint", "memoryview",
        "slice", "aiter", "anext",
        "__import__"
    )

    private val items = buildList {
        keywords.forEach {
            add(CompletionItem(it, it, CompletionKind.KEYWORD))
        }
        builtins.forEach {
            add(CompletionItem(it, it, CompletionKind.BUILTIN))
        }
    }

    fun complete(
        text: String,
        cursor: Int
    ): List<CompletionItem> {
        if (cursor !in 0..text.length) return emptyList()

        val prefixStart = text.substring(0, cursor)
            .indexOfLast { !it.isLetterOrDigit() && it != '_' } + 1

        val prefix = text.substring(prefixStart, cursor)

        if (prefix.isEmpty()) return emptyList()

        if (prefix.first().isDigit()) return emptyList()

        return items.filter {
            it.label.startsWith(prefix, ignoreCase = true)
        }.sortedBy { it.label }
    }
}
// Test
fun main() {
    val engine = CompletionEngine()
    val suggestions = engine.complete("e", 1)
    suggestions.forEach(::println)
}