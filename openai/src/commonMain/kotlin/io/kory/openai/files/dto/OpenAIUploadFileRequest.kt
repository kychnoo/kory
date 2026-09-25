package io.kory.openai.files.dto

import io.kory.core.files.KoryFile
import io.kory.openai.files.model.OpenAIFilePurpose

/**
 * Request for uploading a file to the OpenAI Files API.
 *
 * @property file The file to upload.
 * @property purpose The intended purpose of the file.
 */
data class OpenAIUploadFileRequest(
    val file: KoryFile,
    val purpose: OpenAIFilePurpose
)
