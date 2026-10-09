package io.kory.openai.files.dsl

import io.kory.core.dsl.files.BaseFileSelectionBuilder
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
class OpenAIUploadFileRequestBuilder(val purpose: OpenAIFilePurpose) : BaseFileSelectionBuilder<OpenAIUploadFileRequest>() {

    /**
     * Builds the [OpenAIUploadFileRequest].
     *
     * @return A fully-formed request.
     * @throws FileToUploadNotSelectedException if no file was set.
     */
    override fun build(): OpenAIUploadFileRequest = OpenAIUploadFileRequest(
        file = file ?: throw FileToUploadNotSelectedException("File to upload is not selected"),
        purpose = purpose
    )

    internal fun buildRequest(): OpenAIUploadFileRequest = build()
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
): OpenAIUploadFileRequest = OpenAIUploadFileRequestBuilder(purpose).apply(block).buildRequest()