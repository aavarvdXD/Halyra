package com.aavarvd.halyra.io

object PythonLocator {
    private val  canditates: List<String> by lazy {
        val isWindows = System.getProperty("os.name").lowercase().contains("win")
        if (isWindows) listOf("python", "py", "python3") else listOf("python3", "python")
    }

    @Volatile
    private var cached: String? = null

    fun find(): String? {
        cached?.let { return it }
        return canditates.firstOrNull { works(it) }?.also { cached = it }
    }

    private fun works(command: String): Boolean = try {
        val process = ProcessBuilder(command, "--version")
            .redirectErrorStream(true)
            .start()
        process.inputStream.readBytes()
        process.waitFor() == 0
    } catch (e: Exception) {
        false
    }
}