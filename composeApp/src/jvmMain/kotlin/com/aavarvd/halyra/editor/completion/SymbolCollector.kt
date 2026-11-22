package com.aavarvd.halyra.editor.completion

import com.aavarvd.halyra.editor.lexer.PyLexer
import com.aavarvd.halyra.editor.lexer.TokenType

class SymbolCollector() {
    private val lexer = PyLexer()

    fun collect(text: String): List<CompletionItem> {
        val tokens = lexer.tokenize(text)
            .filter {
                it.type != TokenType.WHITESPACE &&
                it.type != TokenType.COMMENT
            }

        val symbols = mutableListOf<CompletionItem>()

        for (i in tokens.indices) {
            val token = tokens[i]
            val next = tokens.getOrNull(i + 1)
            val previous = tokens.getOrNull(i - 1)

            when {
                token.type == TokenType.KEYWORD &&
                    token.text == "def" &&
                    next?.type == TokenType.IDENTIFIER -> {
                        symbols += CompletionItem(
                            next.text,
                            next.text,
                            CompletionKind.FUNCTION
                        )
                    }
                token.type == TokenType.KEYWORD &&
                    token.text == "class" &&
                    next?.type == TokenType.IDENTIFIER -> {
                        symbols += CompletionItem(
                            next.text,
                            next.text,
                            CompletionKind.CLASS
                        )
                    }

                token.type == TokenType.KEYWORD &&
                    token.text == "for" &&
                    next?.type == TokenType.IDENTIFIER -> {
                    symbols += CompletionItem(
                        next.text,
                        next.text,
                        CompletionKind.VARIABLE
                    )
                }

                token.type == TokenType.IDENTIFIER &&
                next?.type == TokenType.OPERATOR &&
                next.text == "=" -> {
                    symbols += CompletionItem(
                        token.text,
                        token.text,
                        CompletionKind.VARIABLE
                    )
                }
            }
        }

        return symbols.distinctBy { it.label }
    }
}