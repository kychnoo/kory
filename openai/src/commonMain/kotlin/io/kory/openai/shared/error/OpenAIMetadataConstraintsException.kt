package io.kory.openai.shared.error

import io.kory.core.exception.KoryException

class OpenAIMetadataConstraintsException(override val message: String) : KoryException(message)