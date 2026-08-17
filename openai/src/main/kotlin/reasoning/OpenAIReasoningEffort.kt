package io.kory.openai.reasoning

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OpenAIReasoningEffort {
    @SerialName("none")NONE,
    @SerialName("minimal")MINIMAL,
    @SerialName("low")LOW,
    @SerialName("medium")MEDIUM,
    @SerialName("high")HIGH,
    @SerialName("xhigh")XHIGH,
    @SerialName("max")MAX,

}