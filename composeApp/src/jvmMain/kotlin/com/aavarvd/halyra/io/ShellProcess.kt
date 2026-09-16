package com.aavarvd.halyra.io

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter

class ShellProcess(
    private val workingDir: java.io.File,
    private val onOutput: (String) -> Unit,
    private val onFinished: () -> Unit = {}
) {
    private var process: Process? = null
    private var inputWriter: BufferedWriter? = null

    suspend fun start() = withContext(Dispatchers.IO) {
        try {
            val isWindows = System.getProperty("os.name").lowercase().contains("win")

            val command = if (isWindows) {
                listOf("cmd.exe")
            } else {
                listOf("/bin/bash", "-i")
            }

            process = ProcessBuilder(command)
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()

            inputWriter = process!!.outputStream.bufferedWriter()

            val inputStream = process!!.inputStream
            val buffer = ByteArray(1024)

            while (true) {
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) break
                if (bytesRead > 0) {
                    onOutput(String(buffer, 0, bytesRead, Charsets.UTF_8))
                }
            }

            process?.waitFor()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onOutput("\nShell error: ${e.message ?: "Unknown error"}\n")
        } finally {
            inputWriter?.close()
            inputWriter = null
            process = null
            onFinished()
        }
    }

    fun sendInput(input: String) {
        try {
            inputWriter?.apply {
                write(input)
                newLine()
                flush()
            }
        } catch (_: Exception) {
        }
    }

    fun stop() {
        inputWriter?.close()
        inputWriter = null
        process?.let {
            if (it.isAlive) {
                it.destroyForcibly()
            }
        }
        process = null
    }

    fun isRunning(): Boolean {
        return process?.isAlive == true
    }
}