package io.kory.openai.files.dsl

import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.exception.files.upload.FileToUploadNotSelectedException
import io.kory.core.files.KoryFile
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import io.kory.openai.files.model.OpenAIFilePurpose

@OpenAIRequestDsl
class OpenAIUploadFileRequestBuilder(val purpose: OpenAIFilePurpose) {
    private var file: KoryFile? = null

    fun file(file: KoryFile) {
        if (!file.exists()) throw FileNotFoundException("File at path ${file.filePath} not found")
        this.file = file
    }

    fun fileFromPath(path: String) {
        file = KoryFile.fromPath(path)
    }

    fun createFile(filePath: String, rewriteExists: Boolean = false, block: (KoryFile) -> Unit) {
        file = KoryFile.create(filePath, rewriteExists, block)
    }

    internal fun build(): OpenAIUploadFileRequest = OpenAIUploadFileRequest(
        file = file ?: throw FileToUploadNotSelectedException("File to upload is not selected"),
        purpose = purpose
    )
}

fun openAIUploadFileRequest(
    purpose: OpenAIFilePurpose = OpenAIFilePurpose.FINE_TUNE,
    block: OpenAIUploadFileRequestBuilder.() -> Unit
): OpenAIUploadFileRequest = OpenAIUploadFileRequestBuilder(purpose).apply(block).build()