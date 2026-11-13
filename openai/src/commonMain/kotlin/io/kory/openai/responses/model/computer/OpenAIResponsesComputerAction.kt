package io.kory.openai.responses.model.computer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIResponsesComputerAction {
    @Serializable
    @SerialName("click")
    data class Click(
        val button: OpenAIResponsesComputerClick,
        val x: Int,
        val y: Int,
        val keys: List<String> = emptyList(),
    ) : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("double_click")
    data class DoubleClick(
        val keys: List<String> = emptyList(),
        val x: Int,
        val y: Int,
    ) : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("drag")
    data class Drag(
        val path: List<OpenAIResponsesComputerDragPath>,
        val keys: List<String> = emptyList()
    ) : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("keypress")
    data class Keypress(val keys: List<String>) : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("move")
    data class Move(
        val x: Int,
        val y: Int,
        val keys: List<String> = emptyList(),
    ) : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("screenshot")
    data object Screenshot : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("scroll")
    data class Scroll(
        @SerialName("scroll_x") val scrollX: Int,
        @SerialName("scroll_y") val scrollY: Int,
        val x: Int,
        val y: Int,
        val keys: List<String> = emptyList(),
    ) : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("text")
    data class Text(val text: String) : OpenAIResponsesComputerAction

    @Serializable
    @SerialName("wait")
    data object Wait : OpenAIResponsesComputerAction


}