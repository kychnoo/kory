package io.kory.openai.message

import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.tool.OpenAIToolCall
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

/**
 * Polymorphic request message parameter for the OpenAI API.
 *
 * Discriminated by the `"role"` field. Each variant corresponds to a message role:
 * - [User] — Input from the user.
 * - [System] — System prompt.
 * - [Assistant] — Model's previous response (for conversation history).
 * - [Tool] — Tool execution result.
 *
 * @sample examples.openai.chat.BasicChatCall
 */
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("role")
@Serializable
sealed interface OpenAIMessageParam {

    /**
     * A user message.
     *
     * @property content The message content.
     * @property name Optional identifier for the user.
     */
    @Serializable
    @SerialName("user")
    data class User(
        val content: OpenAIChatCompletionContent,
        val name: String? = null
    ) : OpenAIMessageParam

    /**
     * A system message.
     *
     * @property content The system prompt content.
     * @property name Optional identifier.
     */
    @Serializable
    data class System(
        val content: OpenAIChatCompletionContent,
        val name: String? = null
    ) : OpenAIMessageParam

    /**
     * An assistant message (for conversation history).
     *
     * @property content The assistant's response content. `null` when only tool calls are present.
     * @property name Optional identifier.
     * @property toolCalls Tool calls made by the assistant.
     */
    @Serializable
    @SerialName("assistant")
    data class Assistant(
        val content: OpenAIChatCompletionContent? = null,
        val name: String? = null,
        @SerialName("tool_calls") val toolCalls: List<OpenAIToolCall>? = null
    ) : OpenAIMessageParam

    /**
     * A tool result message.
     *
     * @property content The tool execution result.
     * @property toolCallId The ID of the tool call this result responds to.
     * @property name Optional tool name.
     */
    @Serializable
    @SerialName("tool")
    data class Tool(
        val content: OpenAIChatCompletionContent,
        @SerialName("tool_call_id") val toolCallId: String,
        val name: String? = null
    ) : OpenAIMessageParam
}
