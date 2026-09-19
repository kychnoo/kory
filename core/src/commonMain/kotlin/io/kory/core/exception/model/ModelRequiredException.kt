package io.kory.core.exception.model

import io.kory.core.exception.KoryException

class ModelRequiredException(override val message: String) : KoryException(message)