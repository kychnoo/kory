package io.kory.core.exception.files

import io.kory.core.exception.KoryException

class FileAlreadyExistsException(override val message: String) : KoryException(message)