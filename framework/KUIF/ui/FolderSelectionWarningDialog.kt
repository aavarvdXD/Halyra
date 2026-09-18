package com.aavarvd.halyra.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aavarvd.halyra.AppState

@Composable
fun FolderSelectionWarningDialog(appState: AppState) {
    AlertDialog(
        onDismissRequest = {
            appState.showFolderSelectionWarning = false
        },
        title = { Text("Open Folder Required") },
        text = { Text("Please select a folder to use the project structure view. You cannot select a single file for this action.") },
        confirmButton = {
            Button(
                onClick = {
                    appState.showFolderSelectionWarning = false
                }
            ) {
                Text("OK")
            }
        },
        backgroundColor = AppColors.Background,
        contentColor = Color.White
    )
}
