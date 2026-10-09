package io.kory.core.dsl.files

import io.kory.core.files.KoryFile

interface FileSelectionBuilder {
    val file: KoryFile?
    fun file(file: KoryFile)
    fun fileFromPath(path: String)
    fun createFile(filePath: String, rewriteExists: Boolean = false, block: (KoryFile) -> Unit)
}