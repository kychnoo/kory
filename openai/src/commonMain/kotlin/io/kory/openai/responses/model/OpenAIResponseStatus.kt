package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OpenAIResponseStatus {
    @SerialName("completed")COMPLETED,
    @SerialName("failed")FAILED,
    @SerialName("in_progress")IN_PROGRESS,
    @SerialName("cancelled")CANCELLED,
    @SerialName("queued")QUEUED,
    @SerialName("incomplete")INCOMPLETE,
}