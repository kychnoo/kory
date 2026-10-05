package io.kory.core.files

import io.kory.core.chat.request.files.UploadFileRequest
import io.kory.core.dsl.files.UploadFileRequestBuilder
import io.kory.core.files.api.ApiFile
import io.kory.core.files.api.ApiFilesList
import io.kory.core.files.progress.SingleUploadFileProgress
import io.kory.core.files.progress.UploadFileProgressListener

interface FilesAPISupport {
    suspend fun getFiles(
        ids: List<String> = emptyList(),
        pageToken: String? = null,
        concurrency: Int = 10
    ): ApiFilesList
    suspend fun uploadFile(
        request: UploadFileRequest,
        onProgress: UploadFileProgressListener<SingleUploadFileProgress>? = null,
    ): ApiFile
    suspend fun uploadFile(
        onProgress: UploadFileProgressListener<SingleUploadFileProgress>? = null,
        block: UploadFileRequestBuilder.() -> Unit
    ): ApiFile
}