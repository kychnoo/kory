package io.kory.core.exception.files

import kotlinx.io.IOException

/**
 * Thrown when a file cannot be found at the specified path.
 *
 * @property message A description of the error, or `null` if unavailable.
 */
class FileNotFoundException(override val message: String?) : IOException(message)