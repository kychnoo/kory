package io.kory.core.exception.messages

import io.kory.core.exception.KoryException

class MessageRequiredException(override val message: String) : KoryException(message)