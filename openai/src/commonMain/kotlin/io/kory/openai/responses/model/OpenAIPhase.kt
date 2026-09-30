package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OpenAIPhase(val value: String) {
    @SerialName("commentary")COMMENTARY("commentary"),
    @SerialName("final_answer")FINAL_ANSWER("final_answer"),
}