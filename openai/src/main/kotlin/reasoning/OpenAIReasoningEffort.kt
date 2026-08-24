package io.kory.openai.reasoning

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenAI reasoning effort levels.
 *
 * Controls the depth of the model's internal reasoning process.
 * Maps from [io.kory.core.chat.reasoning.ReasoningConfig.Level] via
 * [toOpenAIReasoningEffort][io.kory.openai.extension.toOpenAIReasoningEffort].
 */
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