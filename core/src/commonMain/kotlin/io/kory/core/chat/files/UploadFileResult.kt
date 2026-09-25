package io.kory.core.chat.files

import io.kory.core.contract.file.AIFileResult

sealed interface UploadFileResult {
    val fileName: String

    fun printableOutput(): String
    fun fileIdOrNull(): String?

    data class Success<FO : AIFileResult>(
        override val fileName: String,
        val fileObject: FO
    ) : UploadFileResult {
        override fun printableOutput(): String = "File with name: $fileName successfully uploaded, id: ${fileObject.id}"
        override fun fileIdOrNull(): String = fileObject.id
    }

    data class Failure(
        override val fileName: String,
        val cause: Throwable
    ) : UploadFileResult {
        override fun printableOutput(): String = "File with name: $fileName failed to upload, message: ${cause.message}"
        override fun fileIdOrNull(): String? = null
    }
}