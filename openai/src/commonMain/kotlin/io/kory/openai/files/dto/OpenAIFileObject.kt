package io.kory.openai.files.dto

import io.kory.core.contract.file.AIFileResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A file object returned by the OpenAI Files API.
 *
 * @property id The unique file identifier.
 * @property bytes The file size in bytes.
 * @property createdAt Unix timestamp (in seconds) when the file was created.
 * @property filename The name of the file.
 * @property obj The object type (always `"file"`).
 * @property purpose The intended purpose of the file.
 * @property expiresAt Unix timestamp (in seconds) when the file expires, if applicable.
 */
@Serializable
data class OpenAIFileObject(
    override val id: String,
    val bytes: Long,
    @SerialName("created_at") val createdAt: Long,
    val filename: String,
    @SerialName("object") val obj: String = "file",
    val purpose: String,
    @SerialName("expires_at") val expiresAt: Long? = null
) : AIFileResult
