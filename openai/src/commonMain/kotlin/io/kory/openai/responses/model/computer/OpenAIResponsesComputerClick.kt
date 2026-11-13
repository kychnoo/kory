package io.kory.openai.responses.model.computer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OpenAIResponsesComputerClick {
    @SerialName("left")LEFT,
    @SerialName("right")RIGHT,
    @SerialName("wheel")WHEEL,
    @SerialName("back")BACK,
    @SerialName("forward")FORWARD,
}