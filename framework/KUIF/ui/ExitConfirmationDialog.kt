package com.aavarvd.halyra.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aavarvd.halyra.AppState
import com.aavarvd.halyra.io.FileManager
import com.aavarvd.halyra.io.saveFile
import com.aavarvd.halyra.io.saveFileAs

@Composable
fun ExitConfirmationDialog(
    appState: AppState,
    onCloseRequest: () -> Unit,
    onCloseCancelled: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            appState.showExitConfirmation = false
            onCloseCancelled()
        },
        title = { Text("Unsaved Changes") },
        text = { Text("You have unsaved changes in one or more tabs. Do you want to save all before exiting?") },
        confirmButton = {
            Button(
                onClick = {
                    appState.tabs.forEachIndexed { index, tab ->
                        if (tab.isModified) {
                            if (tab.file != null) {
                                saveFile(tab.file, tab.text.text)
                            } else {
                                appState.activeTabIndex = index
                                saveFileAs(tab.text.text)?.let {
                                    appState.tabs[index] = tab.copy(file = it, lastSavedText = tab.text.text)
                                }
                            }
                        }
                    }
                    appState.showExitConfirmation = false
                    onCloseRequest()
                }
            ) {
                Text("Save All and Exit")
            }
        },
        dismissButton = {
            Row {
                Button(
                    onClick = {
                        appState.showExitConfirmation = false
                        onCloseRequest()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Gray
                    )
                ) {
                    Text("Exit Without Saving")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        appState.showExitConfirmation = false
                        onCloseCancelled()
                    }
                ) {
                    Text("Cancel")
                }
            }
        },
        backgroundColor = AppColors.Background,
        contentColor = Color.White
    )
}
