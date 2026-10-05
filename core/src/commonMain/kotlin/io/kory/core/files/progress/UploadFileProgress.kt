package io.kory.core.files.progress

import kotlin.jvm.JvmInline

/**
 * Base interface for upload progress values.
 */
sealed interface UploadFileProgress {
    /** Progress as a fraction (0.0–1.0). */
    val value: Float
    /** Progress as a percentage (0–100). */
    val percentage: Int
}

/**
 * Progress of a single file upload.
 *
 * @property value Progress as a fraction (0.0–1.0).
 */
@JvmInline
value class SingleUploadFileProgress(override val value: Float) : UploadFileProgress {
    override val percentage: Int get() = (value * 100).toInt()
}

/**
 * Progress of a file upload in a multi-file operation.
 *
 * @property fileName The name of the file being uploaded.
 * @property value Progress as a fraction (0.0–1.0).
 */
data class MultiUploadFileProgress(val fileName: String, override val value: Float) : UploadFileProgress {
    override val percentage: Int get() = (value * 100).toInt()
}