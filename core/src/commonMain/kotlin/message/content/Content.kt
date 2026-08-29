package io.kory.core.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Sealed interface hierarchy modeling all possible content in chat messages.
 *
 * Content is divided into three sub-hierarchies:
 * - [Request] — Content that can be sent to a model (Text, Parts, ToolCall, ToolResult).
 * - [Response] — Content received from a model (Text, Parts, ToolCall, Reasoning).
 * - [StreamResponse] — Content arriving in streaming chunks (Text, ToolCallDelta, Reasoning).
 *
 * The concrete types are:
 * - [Text] — Plain text content.
 * - [ToolCall] — A tool invocation requested by the model.
 * - [ToolCallDelta] — An incremental streaming tool call chunk.
 * - [ToolResult] — The result of executing a tool.
 * - [Reasoning] — Model reasoning/thinking content.
 * - [Parts] — Multi-part content (text + images).
 *
 * @sample examples.core.chat.content.createMessageWithTextContent
 * @sample examples.core.chat.content.processContentResponse
 */
@Serializable
sealed interface Content {
    /**
     * Content that can be sent to a model in a request.
     * @sample examples.core.chat.content.createRequestContent
     * @sample examples.core.chat.content.convertToRequest
     */
    @Serializable
    sealed interface Request : Content
    /**
     * Content received from a model in a response.
     * @sample examples.core.chat.content.processContentResponse
     * @sample examples.core.chat.content.convertToRequest
     */
    @Serializable
    sealed interface Response : Content
    /**
     * Content arriving in streaming chunks.
     * @sample examples.core.chat.content.receivingContentInStreaming
     */
    @Serializable
    sealed interface StreamResponse

    /**
     * Plain text content.
     *
     * Implements [Request], [Response], and [StreamResponse], making it usable
     * in all contexts.
     *
     * @property text The text content.
     *
     * @sample examples.core.chat.content.createMessageWithTextContent
     * @sample examples.core.chat.content.receivingContentInStreaming
     */
    @SerialName("text")
    @Serializable
    data class Text(
        val text: String
    ) : Content, Request, Response, StreamResponse

    /**
     * A tool invocation requested by the model.
     *
     * Contains the tool's unique [id], [name], and serialized [argumentsJson].
     * Implements both [Response] (received from model) and [Request] (can be
     * forwarded back as an assistant message).
     *
     * @property id Unique identifier for this tool call.
     * @property name The name of the tool to execute.
     * @property argumentsJson JSON-serialized arguments for the tool.
     *
     * @sample examples.core.chat.content.useToolCallAndToolResult
     */
    @SerialName("tool_call")
    @Serializable
    data class ToolCall(
        val id: String,
        val name: String,
        val argumentsJson: String
    ) : Response, Request

    /**
     * An incremental streaming tool call chunk.
     *
     * Contains partial data that must be accumulated across multiple chunks
     * to form a complete [ToolCall].
     *
     * @property index The index of this tool call (for multiple concurrent calls).
     * @property id The tool call ID (arrives in the first chunk).
     * @property name The tool name (arrives in the first chunk).
     * @property argumentsChunk A fragment of the JSON arguments.
     */
    data class ToolCallDelta(
        val index: Int,
        val id: String? = null,
        val name: String? = null,
        val argumentsChunk: String? = null,
    ) : StreamResponse

    /**
     * The result of executing a tool, sent back to the model.
     *
     * @property toolCallId The ID of the [ToolCall] this result responds to.
     * @property name The name of the tool that was executed.
     * @property content The result content (typically a string or JSON).
     *
     * @sample examples.core.chat.content.useToolCallAndToolResult
     */
    @SerialName("tool_result")
    @Serializable
    data class ToolResult(
        val toolCallId: String,
        val name: String,
        val content: String
    ) : Request

    /**
     * Model reasoning/thinking content.
     *
     * Contains the model's internal reasoning process (sometimes called "thinking"
     * or "chain-of-thought"). Implements both [Response] and [StreamResponse].
     *
     * @property value The reasoning text.
     *
     * @sample examples.core.chat.content.receivingContentInStreaming
     * @sample examples.core.chat.content.processContentResponse
     */
    @SerialName("reasoning")
    @Serializable
    data class Reasoning (
        val value: String
    ) : Response, StreamResponse

    /**
     * Multi-part content containing a list of [ContentPart] items.
     *
     * Used for messages with mixed content (e.g. text + images).
     * Implements both [Request] and [Response].
     *
     * @property parts The ordered list of content parts.
     *
     * @sample examples.core.chat.content.createContentParts
     * @sample examples.core.chat.content.processContentParts
     */
    @SerialName("parts")
    @Serializable
    data class Parts(
        val parts: List<ContentPart>
    ) : Content, Request, Response
}