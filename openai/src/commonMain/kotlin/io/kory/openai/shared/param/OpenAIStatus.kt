package io.kory.openai.shared.param

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OpenAIStatus {
    @SerialName("completed")COMPLETED,
    @SerialName("in_progress")IN_PROGRESS,
    @SerialName("incomplete")INCOMPLETE,
}