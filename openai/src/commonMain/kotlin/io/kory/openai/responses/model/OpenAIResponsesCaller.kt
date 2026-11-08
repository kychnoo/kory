package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIResponsesCaller {
    @Serializable
    @SerialName("direct")
    object Direct

    @Serializable
    @SerialName("program")
    object Program
}