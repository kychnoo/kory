package io.kory.core.chat.request.files

import io.kory.core.files.KoryFile

/**
 * Request for uploading a file.
 *
 * @property file The file to upload.
 * @property expiresAfterSeconds Optional TTL in seconds. `null` uses provider default.
 */
data class UploadFileRequest(
    val file: KoryFile,
    val expiresAfterSeconds: Long? = null,
)
