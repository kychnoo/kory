package io.kory.core.samples.files

import io.kory.core.files.KoryFile

fun gettingFileFromPath(filePath: String) {
    // Getting files from path.
    val file = KoryFile.fromPath(filePath) // This function can be thrown a FileNotFoundException!

    println(file.mimeTypeStr())
}

fun gettingFileFromPathAsResult(filePath: String) {
    // You can get file as Result<KoryFile> using fromPathCatching function.
    KoryFile.fromPathCatching(filePath).onSuccess { file ->
        println(file.mimeTypeStr())
    }.onFailure { th ->
        println(th)
    }
}

fun creatingFileUsingCreate() {
    KoryFile.create("file.ext") { file ->
        // Use file functions.
        // Example: file.writeBytes(ByteArray(4096))
    }
}