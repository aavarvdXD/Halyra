package com.aavarvd.halyra.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import com.aavarvd.halyra.editor.lexer.PyLexer
import com.aavarvd.halyra.editor.lexer.TokenType
import com.aavarvd.halyra.editor.lexer.PyTokenAnalyzer

class PythonHighLightTransformation : VisualTransformation {
    private val lexer = PyLexer()
    private val analyzer = PyTokenAnalyzer()

    override fun filter(text: AnnotatedString): TransformedText {

        val code = text.text
        val highlighted = AnnotatedString.Builder(code)

        val tokens = analyzer.analyze(
            lexer.tokenize(code)
        )

        tokens.forEach { token ->

            val color = when (token.type) {

                TokenType.KEYWORD       -> Color(0xFFCC7832)
                TokenType.STRING        -> Color(0xFF6A8759)
                TokenType.NUMBER        -> Color(0xFF6897BB)
                TokenType.COMMENT       -> Color(0xFF808080)
                TokenType.BUILTIN       -> Color(0xFF61AFEF)
                TokenType.FUNCTION_NAME -> Color(0xFF56B6C2)
                TokenType.CLASS_NAME    -> Color(0xFFE5C078)
                TokenType.FUNCTION_CALL -> Color(0xFF61AFEF)
                TokenType.SPECIAL_METHOD -> Color(0xFFB200B2)
                TokenType.SPECIAL_ATTRIBUTE, TokenType.SELF -> Color(0xFF94558D)
                else -> null
            }


            if (color != null) {
                highlighted.addStyle(
                    SpanStyle(color = color),
                    token.start,
                    token.end
                )
            }
        }


        return TransformedText(
            highlighted.toAnnotatedString(),
            OffsetMapping.Identity
        )
    }
}
