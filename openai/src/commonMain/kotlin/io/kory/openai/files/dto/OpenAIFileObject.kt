package io.kory.openai.files.dto

import io.kory.core.contract.file.AIFileResult
import io.kory.openai.files.model.OpenAIPurpose
import io.kory.openai.files.serialization.FlexibleInstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

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
    val bytes: Long = 0L,
    @Serializable(with = FlexibleInstantSerializer::class)
    @SerialName("created_at") val createdAt: Long,
    val filename: String = "",
    @SerialName("object") val obj: String = "file",
    val purpose: OpenAIPurpose = OpenAIPurpose.ASSISTANTS,
    @Serializable(with = FlexibleInstantSerializer::class)
    @SerialName("expires_at") val expiresAt: Long? = null
) : AIFileResult {
    /**
     * Returns [createdAt] as an [Instant].
     *
     * @return The creation timestamp.
     */
    fun createdAtAsInstant(): Instant = Instant.fromEpochSeconds(createdAt)

    /**
     * Returns [expiresAt] as an [Instant], or `null` if the file never expires.
     *
     * @return The expiration timestamp, or `null`.
     */
    fun expiresAtAsInstant(): Instant? = if (expiresAt != null) Instant.fromEpochSeconds(expiresAt) else null
}
