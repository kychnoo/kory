package io.kory.openai.message

import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.tool.OpenAIToolCall
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("role")
@Serializable
sealed interface OpenAIMessageParam {

    @Serializable
    @SerialName("user")
    data class User(
        val content: OpenAIChatCompletionContent,
        val name: String? = null
    ) : OpenAIMessageParam

    @Serializable
    data class System(
        val content: OpenAIChatCompletionContent,
        val name: String? = null
    ) : OpenAIMessageParam

    @Serializable
    @SerialName("assistant")
    data class Assistant(
        val content: OpenAIChatCompletionContent? = null,
        val name: String? = null,
        @SerialName("tool_calls") val toolCalls: List<OpenAIToolCall>? = null
    ) : OpenAIMessageParam

    @Serializable
    @SerialName("tool")
    data class Tool(
        val content: OpenAIChatCompletionContent,
        @SerialName("tool_call_id") val toolCallId: String,
        val name: String? = null
    ) : OpenAIMessageParam
}
