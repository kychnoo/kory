package io.kory.openai.responses.io.items

import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIResponseOutputItem {
    val printableContent: String
}
