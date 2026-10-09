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

/**
 * Provider-agnostic interface for file operations.
 *
 * Implemented by providers that support a Files API (upload, download, list, delete).
 *
 * @see ApiFile
 * @see ApiFilesList
 * @see UploadFileRequest
 */
interface FilesAPISupport {
    /**
     * Retrieves file objects by their IDs.
     *
     * If [ids] is empty, returns all files. Failed retrievals are silently skipped.
     *
     * @param ids The list of file IDs to retrieve.
     * @param pageToken Pagination token, if supported by the provider.
     * @param concurrency Maximum number of concurrent requests.
     * @return An [ApiFilesList] containing the retrieved files.
     *
     * @sample io.kory.core.samples.files.gettingAllFilesFromAPI
     * @sample io.kory.core.samples.files.gettingFilesByIds
     * @sample io.kory.core.samples.files.gettingSingleFileUsingFromFilesAPI
     */
    suspend fun getFiles(
        ids: List<String> = emptyList(),
        pageToken: String? = null,
        concurrency: Int = 10
    ): ApiFilesList

    /**
     * Uploads a single file.
     *
     * @param request The upload request containing the file.
     * @param onProgress Optional listener for upload progress (0.0–1.0).
     * @return An [ApiFile] representing the uploaded file.
     *
     * @sample io.kory.core.samples.files.uploadingSingleFileToAPIWithoutDSL
     */
    suspend fun uploadFile(
        request: UploadFileRequest,
        onProgress: UploadFileProgressListener<SingleFileProgress>? = null,
    ): ApiFile

    /**
     * Uploads a single file using the request DSL.
     *
     * @param onProgress Optional listener for upload progress (0.0–1.0).
     * @param block DSL builder for configuring the upload request.
     * @return An [ApiFile] representing the uploaded file.
     *
     * @see UploadFileRequestBuilder
     *
     * @sample io.kory.core.samples.files.uploadingSingleFileToAPIWithDSL
     */
    suspend fun uploadFile(
        onProgress: UploadFileProgressListener<SingleFileProgress>? = null,
        block: UploadFileRequestBuilder.() -> Unit
    ): ApiFile

    /**
     * Uploads multiple files with controlled concurrency.
     *
     * @param requests The list of upload requests.
     * @param maxConcurrency Maximum number of concurrent uploads. Defaults to `5`.
     * @param onProgress Optional listener for per-file upload progress.
     * @return A list of [UploadFileResult] objects (one per request).
     *
     * @sample io.kory.core.samples.files.uploadingFilesToAPIWithoutDSL
     */
    suspend fun uploadFiles(
        requests: List<UploadFileRequest>,
        maxConcurrency: Int = 5,
        onProgress: UploadFileProgressListener<MultiFileProgress>? = null
    ): List<UploadFileResult>

    /**
     * Uploads multiple files using the request DSL.
     *
     * @param maxConcurrency Maximum number of concurrent uploads. Defaults to `5`.
     * @param onProgress Optional listener for per-file upload progress.
     * @param block DSL builder for configuring the upload requests.
     * @return A list of [UploadFileResult] objects (one per request).
     *
     * @see UploadFilesRequestBuilder
     *
     * @sample io.kory.core.samples.files.uploadingFilesToAPIWithDSL
     */
    suspend fun uploadFiles(
        maxConcurrency: Int = 5,
        onProgress: UploadFileProgressListener<MultiFileProgress>? = null,
        block: UploadFilesRequestBuilder.() -> Unit
    ): List<UploadFileResult>

    /**
     * Deletes a single file by its ID.
     *
     * @param fileId The ID of the file to delete.
     * @return A [DeleteApiFileResult] confirming the deletion.
     *
     * @sample io.kory.core.samples.files.deleteFileFromAPI
     */
    suspend fun deleteFile(fileId: String): DeleteApiFileResult


    /**
     * Deletes multiple files with controlled concurrency.
     *
     * @param filesIds The list of file IDs to delete.
     * @param maxConcurrency Maximum number of concurrent deletions. Defaults to `5`.
     * @return A list of [DeleteFileResult] objects (one per file).
     *
     * @sample io.kory.core.samples.files.deleteFilesFromAPI
     */
    suspend fun deleteFiles(filesIds: List<String>, maxConcurrency: Int = 5): List<DeleteFileResult>

    /**
     * Retrieves the raw content of a file as a byte array.
     *
     * **Warning:** Loading large files directly into memory can cause an **OutOfMemoryError**.
     * To stream large files safely, use [streamFileContent] or for download content to file use [downloadFileContentTo].
     *
     * @param fileId The ID of the file.
     * @return The file content.
     *
     * @sample io.kory.core.samples.files.gettingFileContentAsByteArray
     */
    suspend fun getFileContent(fileId: String): ByteArray

    /**
     * Downloads a file's content and writes it to a [KoryFile].
     *
     * @param fileId The ID of the file to download.
     * @param file The destination file.
     * @param onProgress Optional listener for download progress (0.0–1.0).
     * @return The total number of bytes written.
     *
     * @sample io.kory.core.samples.files.downloadingFileContentToFile
     */
    suspend fun downloadFileContentTo(
        fileId: String,
        file: KoryFile,
        onProgress: UploadFileProgressListener<SingleFileProgress>? = null
    ): Long

    /**
     * Streams a file's content as a [Flow] of byte arrays.
     *
     * @param fileId The ID of the file.
     * @return A [Flow] emitting chunks of the file content.
     *
     * @sample io.kory.core.samples.files.gettingFileContentAsFlow
     */
    fun streamFileContent(fileId: String): Flow<ByteArray>
}