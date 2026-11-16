package com.aavarvd.halyra.editor.lexer

class PyTokenAnalyzer {
    private val specialMethods = setOf(
        "__new__", "__init__", "__del__", "__repr__", "__str__", "__bytes__",
        "__format__", "__lt__", "__le__", "__eq__", "__ne__", "__gt__", "__ge__",
        "__hash__", "__bool__", "__getattr__", "__getattribute__", "__setattr__",
        "__delattr__", "__dir__", "__get__", "__set__", "__delete__", "__set_name__",
        "__init_subclass__", "__class_getitem__", "__call__", "__len__", "__length_hint__",
        "__getitem__", "__setitem__", "__delitem__", "__missing__", "__iter__", "__next__",
        "__reversed__", "__contains__", "__add__", "__sub__", "__mul__", "__matmul__",
        "__truediv__", "__floordiv__", "__mod__", "__divmod__", "__pow__", "__lshift__",
        "__rshift__", "__and__", "__xor__", "__or__", "__radd__", "__rsub__", "__rmul__",
        "__rmatmul__", "__rtruediv__", "__rfloordiv__", "__rmod__", "__rdivmod__", "__rpow__",
        "__rlshift__", "__rrshift__", "__rand__", "__rxor__", "__ror__", "__iadd__",
        "__isub__", "__imul__", "__imatmul__", "__itruediv__", "__ifloordiv__", "__imod__",
        "__ipow__", "__ilshift__", "__irshift__", "__iand__", "__ixor__", "__ior__",
        "__neg__", "__pos__", "__abs__", "__invert__", "__complex__", "__int__", "__float__",
        "__index__", "__round__", "__trunc__", "__floor__", "__ceil__", "__enter__", "__exit__",
        "__aenter__", "__aexit__", "__await__", "__aiter__", "__anext__", "__fspath__",
        "__copy__", "__deepcopy__", "__getstate__", "__setstate__", "__reduce__", "__reduce_ex__"
    )

    private val specialAttributes = setOf(
        "__name__", "__main__", "__file__", "__dict__", "__class__", "__doc__",
        "__package__", "__loader__", "__spec__", "__cached__", "__annotations__",
        "__slots__", "__weakref__", "__module__", "__qualname__", "__defaults__",
        "__kwdefaults__", "__code__", "__globals__", "__closure__", "__builtins__"
    )

    fun analyze(tokens: List<Token>): List<Token> {

        val result = tokens.toMutableList()

        for (i in tokens.indices) {

            val token = tokens[i]

            val previous = previousNonWhitespace(tokens, i)
            val next = nextNonWhitespace(tokens, i)

            if (token.type == TokenType.IDENTIFIER) {
                val specialType = when {
                    token.text == "self" -> TokenType.SELF
                    token.text in specialMethods -> TokenType.SPECIAL_METHOD
                    token.text in specialAttributes -> TokenType.SPECIAL_ATTRIBUTE
                    else -> null
                }
                if (specialType != null) {
                    result[i] = token.copy(type = specialType)
                    continue
                }
            }

            if (
                token.type == TokenType.IDENTIFIER &&
                previous?.text == "def"
            ) {
                result[i] = token.copy(
                    type = TokenType.FUNCTION_NAME
                )
                continue
            }

            if (
                token.type == TokenType.IDENTIFIER &&
                previous?.text == "class"
            ) {
                result[i] = token.copy(
                    type = TokenType.CLASS_NAME
                )
                continue
            }

            if (
                token.type == TokenType.IDENTIFIER &&
                next?.type == TokenType.LPAREN
            ) {
                result[i] = token.copy(
                    type = TokenType.FUNCTION_CALL
                )
            }
        }

        return result
    }

    private fun previousNonWhitespace(
        tokens: List<Token>,
        index: Int
    ): Token? {

        var i = index - 1

        while (i >= 0) {

            if (
                tokens[i].type != TokenType.WHITESPACE &&
                tokens[i].type != TokenType.NEWLINE
            ) {
                return tokens[i]
            }

            i--
        }

        return null
    }

    private fun nextNonWhitespace(
        tokens: List<Token>,
        index: Int
    ): Token? {

        var i = index + 1

        while (i < tokens.size) {

            if (
                tokens[i].type != TokenType.WHITESPACE &&
                tokens[i].type != TokenType.NEWLINE
            ) {
                return tokens[i]
            }

            i++
        }

        return null
    }
}
