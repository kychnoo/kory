package io.kory.core.exception.files.upload

import io.kory.core.exception.KoryException

/**
 * Thrown when no file has been selected for upload.
 *
 * @property message A description of the error.
 */
class FileToUploadNotSelectedException(override val message: String) : KoryException(message)