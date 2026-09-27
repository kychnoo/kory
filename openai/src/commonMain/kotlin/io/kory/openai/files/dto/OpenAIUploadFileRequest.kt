package io.kory.openai.files.dto

import io.kory.core.files.KoryFile
import io.kory.openai.files.model.OpenAIExpiresAfter
import io.kory.openai.files.model.OpenAIFilePurpose
import kotlinx.serialization.SerialName

/**
 * Request for uploading a file to the OpenAI Files API.
 *
 * @property file The file to upload.
 * @property purpose The intended purpose of the file.
 * @property expiresAfter The expiration policy for a file.
 */
data class OpenAIUploadFileRequest(
    val file: KoryFile,
    val purpose: OpenAIFilePurpose,
    @SerialName("expires_after") val expiresAfter: OpenAIExpiresAfter? = null
)
