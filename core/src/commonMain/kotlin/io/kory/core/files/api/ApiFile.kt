package io.kory.core.files.api

import io.kory.core.contract.file.AIFileResult
import io.kory.core.files.MimeType
import kotlin.time.Instant

/**
 * Provider-agnostic file object.
 *
 * @property id The unique file identifier.
 * @property name The name of the file.
 * @property mimeType The detected MIME type.
 * @property sizeBytes The file size in bytes.
 * @property createdAt Unix timestamp (in milliseconds) when the file was created.
 * @property expiresAt Unix timestamp (in milliseconds) when the file expires, if applicable.
 */
data class ApiFile(
    override val id: String,
    val name: String,
    val mimeType: MimeType,
    val sizeBytes: Long,
    val createdAt: Long,
    val expiresAt: Long? = null
): AIFileResult {
    /** [createdAt] as an [Instant]. */
    val createdAtAsInstant: Instant get() = Instant.fromEpochMilliseconds(createdAt)

    /** [expiresAt] as an [Instant], or `null` if the file never expires. */
    val expiresAtAsInstant: Instant? get() = expiresAt?.let { Instant.fromEpochMilliseconds(it) }
}
