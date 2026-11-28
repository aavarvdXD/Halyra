package com.aavarvd.halyra.settings

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.aavarvd.halyra.ui.AppColors

@Composable
fun SettingsWindow(
    onCloseRequest: () -> Unit,
    settingsState: SettingsState = remember { SettingsState() }
) {
    DialogWindow(
        onCloseRequest = onCloseRequest,
        title = "Halyra Settings",
        state = rememberDialogState(
            width = 900.dp,
            height = 620.dp
        ),
        resizable = true,
    ) {
        MaterialTheme(
            colors = darkColors(
                primary = AppColors.Divider,
                background = AppColors.Background,
                surface = AppColors.Background,
                onPrimary = Color.White,
                onBackground = AppColors.Text
            )
        ) {
            SettingsScreen(
                state = settingsState,
                onClose = onCloseRequest
            )
        }
    }
}