package com.aavarvd.halyra.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aavarvd.halyra.AppState
import com.aavarvd.halyra.io.FileManager

@Composable
fun NewFileDialog(
    appState: AppState,
    fileManager: FileManager
) {
    var fileName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = {
            appState.showNewFileDialog = false
        },
        title = { Text("New File") },
        text = {
            Column {
                Text("Enter file name:")
                TextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parent = appState.newFileParent ?: appState.projectRoot
                    if (parent != null && fileName.isNotBlank()) {
                        fileManager.createNewFile(parent, fileName)
                    }
                    appState.showNewFileDialog = false
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    appState.showNewFileDialog = false
                }
            ) {
                Text("Cancel")
            }
        },
        backgroundColor = AppColors.Background,
        contentColor = Color.White
    )
}
