package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OpenAIAllowedCallers {
    @SerialName("direct") DIRECT,
    @SerialName("programmatic") PROGRAMMATIC,
}