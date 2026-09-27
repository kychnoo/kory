package io.kory.core.exception.files

import io.kory.core.exception.KoryException
import io.kory.core.files.MimeType

/**
 * Thrown when a file's MIME type is not supported by the current operation.
 *
 * @property mimeType The unsupported MIME type.
 * @property message A description of the error.
 */
class UnsupportedMimeTypeException(
    val mimeType: MimeType,
    override val message: String
) : KoryException(message)