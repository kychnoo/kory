package io.kory.openai.completions.message

import io.kory.core.message.Role
import io.kory.openai.completions.message.content.OpenAIChatCompletionContent
import io.kory.openai.completions.message.content.audio.OpenAIAssistantAudioParam
import io.kory.openai.completions.serialization.message.OpenAIMessageParamCustomSerializer
import io.kory.openai.completions.tool.OpenAIFunctionCall
import io.kory.openai.completions.tool.OpenAIToolCall
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
 * - [Developer] — Developer instructions (replaces system prompts for `o1` and newer models).
 * - [Assistant] — Model's previous response (for conversation history).
 * - [Tool] — Tool execution result.
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
    @SerialName("system")
    data class System(
        val content: OpenAIChatCompletionContent,
        val name: String? = null
    ) : OpenAIMessageParam

    /**
     * Developer-provided instructions that the model follows regardless of user messages.
     *
     * With `o1` models and newer, `developer` messages replace the previous `system` messages.
     * For developer messages only text content parts are supported.
     *
     * @property content The developer message content.
     * @property name Optional identifier for the participant.
     */
    @Serializable
    @SerialName("developer")
    data class Developer(
        val content: OpenAIChatCompletionContent,
        val name: String? = null
    ) : OpenAIMessageParam

    /**
     * An assistant message (for conversation history).
     *
     * @property content The assistant's response content. `null` when only tool calls are present.
     * @property name Optional identifier.
     * @property refusal The refusal message by the assistant. `null` when the model did not refuse.
     * @property audio Data about a previous audio response from the model. `null` when not continuing
     * an audio conversation.
     * @property functionCall Deprecated function call, replaced by [toolCalls].
     * `null` when legacy function calling is not used.
     * @property toolCalls Tool calls made by the assistant.
     */
    @Serializable
    @SerialName("assistant")
    data class Assistant(
        val content: OpenAIChatCompletionContent? = null,
        val name: String? = null,
        val refusal: String? = null,
        val audio: OpenAIAssistantAudioParam? = null,
        @SerialName("function_call") val functionCall: OpenAIFunctionCall? = null,
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

    @Serializable(with = OpenAIMessageParamCustomSerializer::class)
    data class Custom(
        val role: Role,
        val content: OpenAIChatCompletionContent,
    ) : OpenAIMessageParam
}