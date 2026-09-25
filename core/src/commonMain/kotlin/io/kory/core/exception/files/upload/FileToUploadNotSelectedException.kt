package io.kory.core.exception.files.upload

import io.kory.core.exception.KoryException

class FileToUploadNotSelectedException(override val message: String) : KoryException(message)