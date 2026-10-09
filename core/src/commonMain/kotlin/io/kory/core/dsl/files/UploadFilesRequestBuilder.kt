package io.kory.core.dsl.files

import io.kory.core.chat.request.files.UploadFileRequest
import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.files.KoryFile

class UploadFilesRequestBuilder : BaseUploadFilesBuilder<UploadFileRequest>() {
    var defaultExpiresAfterSeconds: Long? = null

    fun file(file: KoryFile, expiresAfter: Long? = defaultExpiresAfterSeconds) {
        validateFile(file)
        fileRequests.add(UploadFileRequest(file, expiresAfter))
    }

    /**
     * Loads a file from the given path and adds it to the upload list.
     *
     * @param path The path to the file.
     * @throws FileNotFoundException if the file does not exist.
     */
    fun fileFromPath(path: String, expiresAfter: Long? = defaultExpiresAfterSeconds) {
        val file = KoryFile.fromPath(path)
        fileRequests.add(UploadFileRequest(file, expiresAfter))
    }

    /**
     * Creates a new file and adds it to the upload list.
     *
     * @param filePath The path to create the file at.
     * @param rewriteExists If `true`, overwrites an existing file. Defaults to `false`.
     * @param block Callback invoked with the created [KoryFile].
     * @throws io.kory.core.exception.files.FileAlreadyExistsException if the file exists and [rewriteExists] is `false`.
     */
    fun createFile(
        filePath: String,
        rewriteExists: Boolean = false,
        expiresAfter: Long? = defaultExpiresAfterSeconds,
        block: (KoryFile) -> Unit
    ) {
        val file = KoryFile.create(filePath, rewriteExists, block)
        fileRequests.add(UploadFileRequest(file, expiresAfter))
    }

    /**
     * Adds a request built via the nested request DSL.
     *
     * @param block DSL builder for configuring the request.
     */
    fun request(block: UploadFileRequestBuilder.() -> Unit) {
        fileRequests.add(uploadFileRequest(block))
    }

    /**
     * Adds a pre-built request to the upload list.
     *
     * @param request The request to add.
     */
    fun request(request: UploadFileRequest) {
        fileRequests.add(request)
    }

    /**
     * Adds multiple pre-built requests to the upload list.
     *
     * @param requests The requests to add.
     */
    fun requests(requests: List<UploadFileRequest>) {
        fileRequests.addAll(requests)
    }

    /**
     * Adds multiple pre-built requests to the upload list.
     *
     * @param requests The requests to add.
     */
    fun requests(vararg requests: UploadFileRequest) {
        fileRequests.addAll(requests)
    }

    fun buildRequest(): List<UploadFileRequest> = build()
}

inline fun uploadFilesRequest(block: UploadFilesRequestBuilder.() -> Unit): List<UploadFileRequest> {
    return UploadFilesRequestBuilder().apply(block).buildRequest()
}