package io.kory.openai.files.dsl

import io.kory.core.dsl.files.BaseUploadFilesBuilder
import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.files.KoryFile
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import io.kory.openai.files.model.OpenAIFilePurpose

/**
 * DSL builder for creating a list of [OpenAIUploadFileRequest].
 *
 * @sample io.kory.openai.samples.files.createUploadFileRequestsUsingBuilder
 * @see openAIUploadFilesRequest
 */
@OpenAIRequestDsl
class OpenAIUploadFilesBuilder : BaseUploadFilesBuilder<OpenAIUploadFileRequest>() {
    /** Default purpose applied to files added without an explicit purpose. */
    var defaultPurpose: OpenAIFilePurpose = OpenAIFilePurpose.FINE_TUNE

    /**
     * Adds a file to the upload list.
     *
     * @param file The file to upload.
     * @param purpose The file's purpose. Defaults to [defaultPurpose].
     * @throws FileNotFoundException if the file does not exist.
     */
    fun file(file: KoryFile, purpose: OpenAIFilePurpose = defaultPurpose) {
        validateFile(file)
        fileRequests.add(OpenAIUploadFileRequest(file, purpose))
    }

    /**
     * Loads a file from the given path and adds it to the upload list.
     *
     * @param path The path to the file.
     * @param purpose The file's purpose. Defaults to [defaultPurpose].
     * @throws FileNotFoundException if the file does not exist.
     */
    fun fileFromPath(path: String, purpose: OpenAIFilePurpose = defaultPurpose) {
        val file = KoryFile.fromPath(path)
        fileRequests.add(OpenAIUploadFileRequest(file, purpose))
    }

    /**
     * Creates a new file and adds it to the upload list.
     *
     * @param filePath The path to create the file at.
     * @param rewriteExists If `true`, overwrites an existing file. Defaults to `false`.
     * @param purpose The file's purpose. Defaults to [defaultPurpose].
     * @param block Callback invoked with the created [KoryFile].
     * @throws FileAlreadyExistsException if the file exists and [rewriteExists] is `false`.
     */
    fun createFile(
        filePath: String,
        rewriteExists: Boolean = false,
        purpose: OpenAIFilePurpose = defaultPurpose,
        block: (KoryFile) -> Unit
    ) {
        val file = KoryFile.create(filePath, rewriteExists, block)
        fileRequests.add(OpenAIUploadFileRequest(file, purpose))
    }

    /**
     * Adds a request built via the nested request DSL.
     *
     * @param purpose The file's purpose. Defaults to [defaultPurpose].
     * @param block DSL builder for configuring the request.
     */
    fun request(purpose: OpenAIFilePurpose = defaultPurpose, block: OpenAIUploadFileRequestBuilder.() -> Unit) {
        fileRequests.add(openAIUploadFileRequest(purpose, block))
    }

    /**
     * Adds a pre-built request to the upload list.
     *
     * @param request The request to add.
     */
    fun request(request: OpenAIUploadFileRequest) {
        fileRequests.add(request)
    }

    /**
     * Adds multiple pre-built requests to the upload list.
     *
     * @param requests The requests to add.
     */
    fun requests(requests: List<OpenAIUploadFileRequest>) {
        fileRequests.addAll(requests)
    }

    /**
     * Adds multiple pre-built requests to the upload list.
     *
     * @param requests The requests to add.
     */
    fun requests(vararg requests: OpenAIUploadFileRequest) {
        fileRequests.addAll(requests)
    }

    fun buildRequest(): List<OpenAIUploadFileRequest> = build()
}

/**
 * Builds a list of [OpenAIUploadFileRequest] using a DSL-style builder.
 *
 * @param block Lambda for configuring the upload requests.
 * @return A list of fully-formed [OpenAIUploadFileRequest] instances.
 *
 * @sample io.kory.openai.samples.files.createUploadFileRequestsUsingBuilder
 */
fun openAIUploadFilesRequest(block: OpenAIUploadFilesBuilder.() -> Unit): List<OpenAIUploadFileRequest> {
    return OpenAIUploadFilesBuilder().apply(block).buildRequest()
}