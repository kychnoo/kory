package io.kory.core.files

import io.kory.core.chat.files.DeleteFileResult
import io.kory.core.files.api.DeleteApiFileResult
import io.kory.core.chat.files.UploadFileResult
import io.kory.core.chat.request.files.UploadFileRequest
import io.kory.core.contract.file.AIDeleteFileResult
import io.kory.core.dsl.files.UploadFileRequestBuilder
import io.kory.core.dsl.files.UploadFilesRequestBuilder
import io.kory.core.files.api.ApiFile
import io.kory.core.files.api.ApiFilesList
import io.kory.core.files.progress.MultiFileProgress
import io.kory.core.files.progress.SingleFileProgress
import io.kory.core.files.progress.UploadFileProgressListener
import kotlinx.coroutines.flow.Flow

interface FilesAPISupport {
    suspend fun getFiles(
        ids: List<String> = emptyList(),
        pageToken: String? = null,
        concurrency: Int = 10
    ): ApiFilesList
    suspend fun uploadFile(
        request: UploadFileRequest,
        onProgress: UploadFileProgressListener<SingleFileProgress>? = null,
    ): ApiFile
    suspend fun uploadFile(
        onProgress: UploadFileProgressListener<SingleFileProgress>? = null,
        block: UploadFileRequestBuilder.() -> Unit
    ): ApiFile

    suspend fun uploadFiles(
        requests: List<UploadFileRequest>,
        maxConcurrency: Int = 5,
        onProgress: UploadFileProgressListener<MultiFileProgress>? = null
    ): List<UploadFileResult>

    suspend fun uploadFiles(
        maxConcurrency: Int = 5,
        onProgress: UploadFileProgressListener<MultiFileProgress>? = null,
        block: UploadFilesRequestBuilder.() -> Unit
    ): List<UploadFileResult>

    suspend fun deleteFile(fileId: String): DeleteApiFileResult

    suspend fun deleteFiles(filesIds: List<String>): List<DeleteFileResult>

    suspend fun getFileContent(fileId: String): ByteArray

    suspend fun downloadFileContentTo(
        fileId: String,
        file: KoryFile,
        onProgress: UploadFileProgressListener<SingleFileProgress>? = null
    ): Long

    fun streamFileContent(fileId: String): Flow<ByteArray>
}