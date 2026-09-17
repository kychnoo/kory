package io.kory.openai.exception.modality

import io.kory.core.exception.KoryException

class OpenAIMetadataConstraintsException(override val message: String) : KoryException(message)