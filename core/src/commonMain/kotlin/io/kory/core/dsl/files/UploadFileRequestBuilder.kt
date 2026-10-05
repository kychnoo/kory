package io.kory.core.dsl.files

import io.kory.core.chat.request.files.UploadFileRequest
import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.exception.files.upload.FileToUploadNotSelectedException

/**
 * DSL builder for [UploadFileRequest].
 *
 * @see UploadFileRequest
 */
@KoryDsl
class UploadFileRequestBuilder : BaseFileSelectionBuilder<UploadFileRequest>() {
    /** Optional TTL for the uploaded file in seconds. `null` uses provider default. */
    var expiresAfterSeconds: Long? = null

    override fun build(): UploadFileRequest = UploadFileRequest(
        file = file ?: throw FileToUploadNotSelectedException("File to upload is not selected"),
        expiresAfterSeconds = expiresAfterSeconds,
    )

    internal fun buildRequest(): UploadFileRequest = build()
}

fun uploadFileRequest(block: UploadFileRequestBuilder.() -> Unit): UploadFileRequest {
    return UploadFileRequestBuilder().apply(block).buildRequest()
}