package io.kory.core.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Content {
    @Serializable
    sealed interface Request : Content
    @Serializable
    sealed interface Response : Content

    @Serializable
    sealed interface StreamResponse

    @SerialName("text")
    @Serializable
    data class Text(
        val text: String
    ) : Content, Request, Response, StreamResponse

    @SerialName("tool_call")
    @Serializable
    data class ToolCall(
        val id: String,
        val name: String,
        val argumentsJson: String
    ) : Response, Request

    data class ToolCallDelta(
        val index: Int,
        val id: String? = null,
        val name: String? = null,
        val argumentsChunk: String? = null,
    ) : StreamResponse

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
    ) : Response, StreamResponse

    @SerialName("parts")
    @Serializable
    data class Parts(
        val parts: List<ContentPart>
    ) : Content, Request, Response
}