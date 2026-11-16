package com.aavarvd.halyra.editor.lexer

enum class TokenType {

    FUNCTION_NAME,
    CLASS_NAME,
    FUNCTION_CALL,
    SPECIAL_METHOD,
    SPECIAL_ATTRIBUTE,
    SELF,

    KEYWORD,
    IDENTIFIER,
    BUILTIN,

    STRING,
    NUMBER,
    COMMENT,

    OPERATOR,

    LPAREN,
    RPAREN,

    LBRACKET,
    RBRACKET,

    LBRACE,
    RBRACE,

    COMMA,
    COLON,
    DOT,

    WHITESPACE,
    NEWLINE,

    UNKNOWN
}

data class Token(
    val type: TokenType,
    val start: Int,
    val end: Int,
    val text: String
)
