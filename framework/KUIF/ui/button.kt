package com.aavarvd.halyra.ui

import androidx.compose.runtime.Composable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.background

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    /*Button(
        onClick = onClick,
        elevation = ButtonDefaults.elevation(0.dp),
        shape = RoundedCornerShape(4.dp),

        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (hovered) Color(0xFF515151) else Color(0xFF2B2B2B),
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontFamily = AppFonts.Inter
        )
    }*/

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (hovered) Color(0xFF515151) else Color(0xFF2B2B2B))
            .hoverable(interactionSource)
            .clickable(
                interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontFamily = AppFonts.Inter
        )
    }
}