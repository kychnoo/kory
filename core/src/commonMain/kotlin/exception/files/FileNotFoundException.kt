package io.kory.core.exception.files

import kotlinx.io.IOException

class FileNotFoundException(override val message: String?) : IOException(message)