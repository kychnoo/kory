package io.kory.openai.responses.io.items

import io.kory.openai.responses.serialization.OpenAIResponseInputItemSerializer
import kotlinx.serialization.Serializable

/**
 * A list of one or many input items to the model, containing different content types.
 */
@Serializable(with = OpenAIResponseInputItemSerializer::class)
sealed interface OpenAIResponseInputItem