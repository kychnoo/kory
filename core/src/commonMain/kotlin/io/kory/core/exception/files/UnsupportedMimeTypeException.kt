package io.kory.core.exception.files

import io.kory.core.exception.KoryException
import io.kory.core.files.MimeType

class UnsupportedMimeTypeException(
    val mimeType: MimeType,
    override val message: String
) : KoryException(message)