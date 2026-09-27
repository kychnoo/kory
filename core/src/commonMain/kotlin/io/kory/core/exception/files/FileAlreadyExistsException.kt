package io.kory.core.exception.files

import io.kory.core.exception.KoryException

/**
 * Thrown when attempting to create a file that already exists.
 *
 * @property message A description of the error.
 */
class FileAlreadyExistsException(override val message: String) : KoryException(message)