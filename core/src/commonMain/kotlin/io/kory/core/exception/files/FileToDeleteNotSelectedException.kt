package io.kory.core.exception.files

import io.kory.core.exception.KoryException

class FileToDeleteNotSelectedException(override val message: String, override val cause: Throwable?) : KoryException(message)