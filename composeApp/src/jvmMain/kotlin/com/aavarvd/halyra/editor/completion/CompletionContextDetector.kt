package com.aavarvd.halyra.editor.completion

import com.aavarvd.halyra.editor.lexer.PyLexer
import com.aavarvd.halyra.editor.lexer.TokenType

class CompletionContextDetector() {
    private val lexer = PyLexer()

    fun detect(text: String, cursor: Int): CompletionContext {
        if (cursor !in 0..text.length) return CompletionContext.NONE

        val tokens = lexer.tokenize(text)

        val restrictedToken = tokens.firstOrNull { token ->
            when (token.type) {
                TokenType.STRING ->
                    cursor >= token.start && cursor < token.end

                TokenType.COMMENT ->
                    cursor >= token.start && cursor <= token.end

                else -> false
            }
        }

        if (restrictedToken != null) return CompletionContext.NONE

        val prefixStart = text.substring(0, cursor)
            .indexOfLast { !it.isLetterOrDigit() && it != '_' } + 1

        val previousToken = tokens.lastOrNull {
            it.end <= prefixStart &&
                it.type != TokenType.WHITESPACE &&
                it.type != TokenType.NEWLINE
        }

        if (previousToken?.type == TokenType.DOT) return CompletionContext.MEMBER

        return CompletionContext.GENERAL
    }
}