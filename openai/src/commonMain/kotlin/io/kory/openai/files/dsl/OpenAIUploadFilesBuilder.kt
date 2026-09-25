package io.kory.openai.files.dsl

import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.files.KoryFile
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import io.kory.openai.files.model.OpenAIFilePurpose

@OpenAIRequestDsl
class OpenAIUploadFilesBuilder {
    private val fileRequests = linkedSetOf<OpenAIUploadFileRequest>()

    var defaultPurpose: OpenAIFilePurpose = OpenAIFilePurpose.FINE_TUNE

    fun file(file: KoryFile, purpose: OpenAIFilePurpose = defaultPurpose) {
        if (!file.exists()) throw FileNotFoundException("File at path ${file.filePath} not found")
        fileRequests.add(OpenAIUploadFileRequest(file, purpose))
    }

    fun fileFromPath(path: String, purpose: OpenAIFilePurpose = defaultPurpose) {
        val file = KoryFile.fromPath(path)
        fileRequests.add(OpenAIUploadFileRequest(file, purpose))
    }

    fun createFile(
        filePath: String,
        rewriteExists: Boolean = false,
        purpose: OpenAIFilePurpose = defaultPurpose,
        block: (KoryFile) -> Unit
    ) {
        val file = KoryFile.create(filePath, rewriteExists, block)
        fileRequests.add(OpenAIUploadFileRequest(file, purpose))
    }

    fun request(purpose: OpenAIFilePurpose = defaultPurpose, block: OpenAIUploadFileRequestBuilder.() -> Unit) {
        fileRequests.add(openAIUploadFileRequest(purpose, block))
    }

    fun request(request: OpenAIUploadFileRequest) {
        fileRequests.add(request)
    }

    fun requests(requests: List<OpenAIUploadFileRequest>) {
        fileRequests.addAll(requests)
    }

    fun requests(vararg requests: OpenAIUploadFileRequest) {
        fileRequests.addAll(requests)
    }

    internal fun build(): List<OpenAIUploadFileRequest> {
        return fileRequests.toList()
    }
}

fun openAIUploadFilesRequest(block: OpenAIUploadFilesBuilder.() -> Unit): List<OpenAIUploadFileRequest> {
    return OpenAIUploadFilesBuilder().apply(block).build()
}