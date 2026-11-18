package com.aavarvd.halyra.editor.lexer

class PyLexer {
    private val keywords = setOf(
        "False", "None", "True",
        "and", "as", "assert", "async", "await",
        "break",
        "class", "continue",
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
        "print",
        "input",
        "len",
        "range",
        "sum",
        "min",
        "max",
        "abs",
        "open",
        "type",
        "int",
        "float",
        "str",
        "list",
        "dict",
        "set"
    )

    private val operators = listOf(
        "**=", "//=",

        "==", "!=", "<=", ">=",

        "+=", "-=", "*=", "/=",
        "%=",

        "**", "//",

        "<<", ">>",

        "&=", "|=", "^=",

        ":=",

        "+", "-", "*", "/",
        "%", "=", "<", ">",
        "&", "|", "^", "~"
    )

    private val stringPrefixes = setOf(
        "r", "u", "b", "f",
        "br", "rb",
        "fr", "rf"
    )

    private fun findStringStart(code: String, index: Int): Int? {
        if (index >= code.length) return null

        if (code[index] == '"' || code[index] == '\'')
            return index

        for (length in 1..2) {
            val end = index + length
            if (end >= code.length) continue

            val prefix = code.substring(index, end).lowercase()
            if (prefix in stringPrefixes && (code[end]== '"' || code[end] == '\'')) return end
        }
        return null
    }

    private fun scanString(code: String, start: Int, quoteIndex: Int): Int {
        val quote = code[quoteIndex]

        val isTtriple =
            quoteIndex + 2 < code.length &&
            code[quoteIndex + 1] == quote &&
            code[quoteIndex + 2] == quote

        val delimiter = if (isTtriple) 3 else 1

        var i = quoteIndex + delimiter

        while (i < code.length) {
            if (code[i] == '\\') {
                i += 2
                continue
            }

            if (
                code[i] == quote &&
                (!isTtriple || (
                    i + 2 < code.length &&
                    code[i + 1] == quote &&
                    code[i + 2] == quote
                ))
            ) return i + delimiter

            if (!isTtriple && code[i] == '\n') return i

            i++
        }

        return code.length
    }

    private fun scanNumber(code: String, start: Int): Int {
        var i = start

        if (
            i + 1 < code.length &&
            code[i] == '0' &&
            code[i + 1].lowercaseChar() in "xbo"
        ) {
            val base = code[i + 1].lowercaseChar()
            i += 2

            while (i < code.length) {
                val c = code[i]

                val valid = when (base) {
                    'x' -> c.isDigit() || c.lowercaseChar() in 'a'..'f'
                    'b' -> c == '0' || c == '1'
                    'o' -> c in '0'..'7'
                    else -> false
                }

                if (valid || c == '_') i++ else break
            }

            return i
        }

        while (i < code.length && (code[i].isDigit() || code[i] == '_')) i++

        if (i < code.length && code[i] == '.') {
            i++

            while (i < code.length && (code[i].isDigit() || code[i] == '_')) i++
        }

        if (i < code.length && code[i].lowercaseChar() == 'e') {
            i++

            if (i < code.length && (code[i] == '+' || code[i] == '-')) i++

            while (i < code.length && (code[i].isDigit() || code[i] == '_')) i++
        }

        // Complex suffix
        if (i < code.length && code[i].lowercaseChar() == 'j') i++

        return i
    }

    fun tokenize(code: String): List<Token> {
        val tokens = mutableListOf<Token>()

        var i = 0
        var iterations = 0
        while (i < code.length) {

            iterations++
            if (iterations % 5000 == 0) println("iterations=$iterations i=$i len=${code.length}")
            val c = code[i]

            val matchedOperator = operators.firstOrNull {
                code.startsWith(it, i)
            }

            if (matchedOperator != null) {
                tokens += Token(
                    TokenType.OPERATOR,
                    i,
                    i + matchedOperator.length,
                    matchedOperator
                )

                i += matchedOperator.length
                continue
            }

            when {

                // String
                findStringStart(code, i) != null -> {
                    val start = i
                    val quoteIndex = findStringStart(code, i)!!

                    i = scanString(code, start, quoteIndex)

                    tokens += Token(
                        TokenType.STRING,
                        start,
                        i,
                        code.substring(start, i)
                    )
                }

                c == '#' -> {

                    val start = i

                    while (i < code.length && code[i] != '\n') {
                        i++
                    }

                    tokens += Token(
                        TokenType.COMMENT,
                        start,
                        i,
                        code.substring(start, i)
                    )
                }

                c.isLetter() || c == '_' -> {
                    val start = i

                    while (i < code.length && (code[i].isLetterOrDigit() || code[i] == '_')) i++

                    val text = code.substring(start, i)

                    tokens += Token(
                        when {
                            text in keywords -> TokenType.KEYWORD
                            text in builtins -> TokenType.BUILTIN
                            else -> TokenType.IDENTIFIER
                        },
                        start,
                        i,
                        text
                    )
                }

                c.isDigit() -> {
                    val start = i

                    i = scanNumber(code, start)

                    tokens += Token(
                        TokenType.NUMBER,
                        start,
                        i,
                        code.substring(start, i)
                    )
                }

                c == ' ' || c == '\t' -> {
                    val start = i

                    while (i < code.length && (code[i] == ' ' || code[i] == '\t')) {
                        i++
                    }

                    tokens += Token(
                        TokenType.WHITESPACE,
                        start,
                        i,
                        code.substring(start, i)
                    )
                }

                c == '\n' -> {
                    tokens += Token(
                        TokenType.NEWLINE,
                        i,
                        i + 1,
                        "\n"
                    )
                    i++
                }

                c == '(' -> {
                    tokens += Token(
                        TokenType.LPAREN,
                        i,
                        i + 1,
                        "("
                    )
                    i++
                }

                c == ')' -> {
                    tokens += Token(
                        TokenType.RPAREN,
                        i,
                        i + 1,
                        ")"
                    )
                    i++
                }

                c == '[' -> {
                    tokens += Token(
                        TokenType.LBRACKET,
                        i,
                        i + 1,
                        "["
                    )
                    i++
                }

                c == ']' -> {
                    tokens += Token(
                        TokenType.RBRACKET,
                        i,
                        i + 1,
                        "]"
                    )
                    i++
                }

                c == '{' -> {
                    tokens += Token(
                        TokenType.LBRACE,
                        i,
                        i + 1,
                        "{"
                    )
                    i++
                }

                c == '}' -> {
                    tokens += Token(
                        TokenType.RBRACE,
                        i,
                        i + 1,
                        "}"
                    )
                    i++
                }

                c == ':' -> {
                    tokens += Token(
                        TokenType.COLON,
                        i,
                        i + 1,
                        ":"
                    )
                    i++
                }

                c == ',' -> {
                    tokens += Token(
                        TokenType.COMMA,
                        i,
                        i + 1,
                        ","
                    )
                    i++
                }

                c == '.' -> {
                    tokens += Token(
                        TokenType.DOT,
                        i,
                        i + 1,
                        "."
                    )
                    i++
                }

                else -> {
                    tokens += Token(
                        type = TokenType.UNKNOWN,
                        start = i,
                        end = i + 1,
                        text = c.toString()
                    )

                    i++
                }
            }
        }
        println("PyLexer: done, iterations=$iterations for ${code.length} chars")
        return tokens
    }
}

fun main() {
    val lexer = PyLexer()

//    val tokens = lexer.tokenize(
//        """
//        def hello(name):
//            print("Hello")
//            score = 42
//            pi = 3.14159265358979
//        """.trimIndent()
//    )
    val tokens = lexer.tokenize(
        """
    message = "Hello \"world\""
    path = r"C:\Users\Player"
    data = b"Hello"
    formatted = f"Hello {name}"
    combined = fr"Hello {name}"
    multiline = """ + "\"\"\"" + """
    Hello
    world
    """ + "\"\"\"" + """
    single = '''Hello
    world'''
    integer = 42
    decimal = 3.14159
    hexadecimal = 0xFF
    binary = 0b1010
    octal = 0o17
    scientific = 1.5e-3
    underscores = 1_000_000
    complex = 3j
    """.trimIndent()
    )

    tokens.forEach(::println)
}