package io.kory.openai.responses.io.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A reference to an existing item (by ID) used as input to the model,
 * instead of repeating the full item content.
 *
 * @property id The ID of the referenced item.
 */
@Serializable
@SerialName("item_reference")
data class OpenAIResponsesItemReference(
    val id: String,
) : OpenAIResponseInputItem
