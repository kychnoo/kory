package io.kory.core.chat.files

import io.kory.core.contract.file.AIDeleteFileResult

sealed interface DeleteFileResult {

    fun printableOutput(): String
    fun idOrNull(): String? = null

    data class Success<T : AIDeleteFileResult>(
        val result: T
    ) : DeleteFileResult {
        override fun printableOutput(): String = "File successfully deleted."
        override fun idOrNull(): String = result.id
    }

    data class Failure(val cause: Throwable) : DeleteFileResult {
        override fun printableOutput(): String = "Error during deleting file, cause: ${cause.message}"
        override fun idOrNull(): String? = null
    }
}