package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OpenAIFileSearchStatus {
    @SerialName("completed") COMPLETED,
    @SerialName("in_progress") IN_PROGRESS,
    @SerialName("incomplete") INCOMPLETE,
    @SerialName("searching") SEARCHING,
    @SerialName("failed") FAILED,
}