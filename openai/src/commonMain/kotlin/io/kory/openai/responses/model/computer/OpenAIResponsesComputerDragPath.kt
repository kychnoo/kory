package io.kory.openai.responses.model.computer

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesComputerDragPath(
    val x: Int,
    val y: Int,
)