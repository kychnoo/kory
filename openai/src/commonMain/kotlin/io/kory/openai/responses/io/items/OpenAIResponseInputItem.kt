package io.kory.openai.responses.io.items

import kotlinx.serialization.Serializable

/**
 * A list of one or many input items to the model, containing different content types.
 */
@Serializable
sealed interface OpenAIResponseInputItem