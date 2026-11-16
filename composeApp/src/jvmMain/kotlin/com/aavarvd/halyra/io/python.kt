package com.aavarvd.halyra.io

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.ByteArrayOutputStream

class PythonProcess(
    private val code: String? = null,
    private val file: File? = null,
    private val onOutput: (String) -> Unit,
    private val onFinished: (Int?) -> Unit = {}
) {
    private var process: Process? = null
    private var inputWriter: BufferedWriter? = null

    suspend fun start() = withContext(Dispatchers.IO) {
        var tempFile: File? = null
        var exitCode: Int? = null
        try {
            val python = PythonLocator.find()
            if (python == null) {
                onOutput (
                    "Python was not found. Install Python 3 and make sure it is on your PATH\n"
                )
                return@withContext
            }
            val scriptFile = if (file != null) {
                file
            } else if (code != null) {
                tempFile = File.createTempFile("halyra_proto_", ".py")
                tempFile.writeText(code)
                tempFile
            } else {
                return@withContext
            }

            val builder = ProcessBuilder(
                python,
                "-u",
                scriptFile.absolutePath
            )
                .redirectErrorStream(true)

            // Desktop-launched apps often don't inherit variables from interactive shell
            // startup files such as ~/.bashrc. Import that environment for Python runs.
            if (!System.getProperty("os.name").lowercase().contains("win")) {
                builder.environment().putAll(ShellEnvironment.read())
            }

            process = builder.start()

            inputWriter = process!!
                .outputStream
                .bufferedWriter()

            val inputStream = process!!.inputStream
            val buffer = ByteArray(1024)

            while (true) {
                val bytesRead = inputStream.read(buffer)

                if (bytesRead == -1) {
                    break
                }

                if (bytesRead > 0) {
                    onOutput(
                        String(
                            buffer,
                            0,
                            bytesRead,
                            Charsets.UTF_8
                        )
                    )
                }
            }

            exitCode = process?.waitFor()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onOutput(
                "\nError running Python: ${e.message ?: "Unknown error"}\n"
            )
        } finally {
            inputWriter?.close()
            inputWriter = null

            process = null
            tempFile?.delete()
            onFinished(exitCode)
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

private object ShellEnvironment {
    @Volatile private var cached: Map<String, String>? = null

    fun read(): Map<String, String> {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: load().also { cached = it }
        }
    }

    private fun load(): Map<String, String> {
        return try {
            val shell = ProcessBuilder("/bin/bash", "-ic", "env -0")
                .redirectError(java.io.File("/dev/null"))
                .start()
            val output = ByteArrayOutputStream()
            shell.inputStream.use { it.copyTo(output) }
            if (shell.waitFor() != 0) return emptyMap()

            output.toString(Charsets.UTF_8).split('\u0000').mapNotNull { entry ->
                val separator = entry.indexOf('=')
                if (separator <= 0) null else entry.substring(0, separator) to entry.substring(separator + 1)
            }.toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
