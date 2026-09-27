package io.kory.openai.files.dsl

import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.exception.files.upload.FileToUploadNotSelectedException
import io.kory.core.files.KoryFile
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import io.kory.openai.files.model.OpenAIFilePurpose

/**
 * DSL builder for [OpenAIUploadFileRequest].
 *
 * @property purpose The intended purpose of the file.
 *
 * @sample io.kory.openai.samples.files.createUploadFileRequestUsingBuilder
 * @see openAIUploadFileRequest
 */
@OpenAIRequestDsl
class OpenAIUploadFileRequestBuilder(val purpose: OpenAIFilePurpose) {
    private var file: KoryFile? = null

    /**
     * Sets the file to upload.
     *
     * @param file The file to upload.
     * @throws FileNotFoundException if the file does not exist.
     */
    fun file(file: KoryFile) {
        if (!file.exists()) throw FileNotFoundException("File at path ${file.filePath} not found")
        this.file = file
    }

    /**
     * Loads the file from the given path and sets it for upload.
     *
     * @param path The path to the file.
     * @throws FileNotFoundException if the file does not exist.
     */
    fun fileFromPath(path: String) {
        file = KoryFile.fromPath(path)
    }

    /**
     * Creates a new file at the given path and sets it for upload.
     *
     * @param filePath The path to create the file at.
     * @param rewriteExists If `true`, overwrites an existing file. Defaults to `false`.
     * @param block Callback invoked with the created [KoryFile].
     * @throws io.kory.core.exception.files.FileAlreadyExistsException if the file exists and [rewriteExists] is `false`.
     */
    fun createFile(filePath: String, rewriteExists: Boolean = false, block: (KoryFile) -> Unit) {
        file = KoryFile.create(filePath, rewriteExists, block)
    }

    /**
     * Builds the [OpenAIUploadFileRequest].
     *
     * @return A fully-formed request.
     * @throws FileToUploadNotSelectedException if no file was set.
     */
    internal fun build(): OpenAIUploadFileRequest = OpenAIUploadFileRequest(
        file = file ?: throw FileToUploadNotSelectedException("File to upload is not selected"),
        purpose = purpose
    )
}

/**
 * Builds an [OpenAIUploadFileRequest] using a DSL-style builder.
 *
 * @param purpose The intended purpose of the file. Defaults to [OpenAIFilePurpose.FINE_TUNE].
 * @param block Lambda for configuring the upload request.
 * @return A fully-formed [OpenAIUploadFileRequest].
 * @throws FileToUploadNotSelectedException if no file was set.
 *
 * @sample io.kory.openai.samples.files.createUploadFileRequestUsingBuilder
 */
fun openAIUploadFileRequest(
    purpose: OpenAIFilePurpose = OpenAIFilePurpose.FINE_TUNE,
    block: OpenAIUploadFileRequestBuilder.() -> Unit
): OpenAIUploadFileRequest = OpenAIUploadFileRequestBuilder(purpose).apply(block).build()