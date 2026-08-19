package io.kory.core.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Content {
    @Serializable
    sealed interface Request : Content
    @Serializable
    sealed interface Response : Content

    @SerialName("text")
    @Serializable
    data class Text(
        val text: String
    ) : Content, Request, Response

    @SerialName("tool_call")
    @Serializable
    data class ToolCall(
        val id: String,
        val name: String,
        val argumentsJson: String
    ) : Response, Request

    @SerialName("tool_result")
    @Serializable
    data class ToolResult(
        val toolCallId: String,
        val name: String,
        val content: String
    ) : Request

    @SerialName("reasoning")
    @Serializable
    data class Reasoning (
        val value: String
    ) : Response

    @SerialName("parts")
    @Serializable
    data class Parts(
        val parts: List<ContentPart>
    ) : Content, Request, Response
}