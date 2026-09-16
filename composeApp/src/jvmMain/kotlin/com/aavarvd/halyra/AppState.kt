package com.aavarvd.halyra

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*
import com.aavarvd.halyra.editor.EditorTab
import com.aavarvd.halyra.editor.search.SearchState
import com.aavarvd.halyra.io.PythonProcess
import kotlinx.coroutines.Job
import java.io.File
import com.aavarvd.halyra.io.ShellProcess
import kotlinx.coroutines.launch

class AppState {
    val tabs = mutableStateListOf<EditorTab>()
    var activeTabIndex by mutableStateOf(0)

    val activeTab: EditorTab?
        get() = tabs.getOrNull(activeTabIndex)

    var searchState by mutableStateOf(SearchState())
    var output by mutableStateOf("")
    var terminalInput by mutableStateOf("")
    var pythonRunJob by mutableStateOf<Job?>(null)
    var pythonProcess by mutableStateOf<PythonProcess?>(null)
    var shellVisible by mutableStateOf(true)
    var projectTreeVisible by mutableStateOf(false)
    var projectRoot by mutableStateOf<java.io.File?>(null)
    var showExitConfirmation by mutableStateOf(false)
    var showFolderSelectionWarning by mutableStateOf(false)
    var terminalHeightPx by mutableStateOf(0f)

    val selectedFiles = mutableStateListOf<java.io.File>()
    var clipboardFiles by mutableStateOf<List<java.io.File>>(emptyList())
    var isCutOperation by mutableStateOf(false)
    var showNewFileDialog by mutableStateOf(false)
    var showFolderPickerDialog by mutableStateOf(false)
    var newFileParent by mutableStateOf<java.io.File?>(null)
    var isLoadingFile by mutableStateOf(false)
    var loadingFileName by mutableStateOf<String?>(null)
    var pendingLargeFile by mutableStateOf<File?>(null)
    var pasteErrorMessage by mutableStateOf<String?>(null)
    var projectTreeRefreshTrigger by mutableStateOf(0)

    private val scrollStates = mutableMapOf<Long, ScrollState>()

    fun scrollStateFor(tab: EditorTab): ScrollState {
        return scrollStates.getOrPut(tab.id) { ScrollState(0) }
    }

    fun removeScrollStateFor(tab: EditorTab) {
        scrollStates.remove(tab.id)
    }

    enum class BottomPanelTab { OUTPUT, TERMINAL }

    var bottomPanelTab by mutableStateOf(BottomPanelTab.OUTPUT)
    var shellOutput by mutableStateOf("")
    var shellInput by mutableStateOf("")
    var shellProcess by mutableStateOf<ShellProcess?>(null)

    val anyModified: Boolean
        get() = tabs.any { it.isModified }

    fun startShell(coroutineScope: kotlinx.coroutines.CoroutineScope) {
        shellProcess?.stop()
        shellOutput = ""
        val shell = ShellProcess(
            workingDir = projectRoot ?: File(System.getProperty("user.home")),
            onOutput = { shellOutput += it },
            onFinished = { shellProcess = null }
        )
        shellProcess = shell
        coroutineScope.launch { shell.start() }
    }
}

@Composable
fun rememberAppState(): AppState {
    return remember { AppState() }
}