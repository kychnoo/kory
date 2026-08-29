package io.kory.core.chat

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.chat.request.ChatRequest
import io.kory.core.dsl.tool.ToolsBuilder
import io.kory.core.dsl.tool.koryTools
import io.kory.core.message.Message
import io.kory.core.tool.KoryTool
import kotlinx.serialization.Serializable

/**
 * An immutable conversation container holding a list of [Message] objects and a model name.
 *
 * This is the primary data structure for representing a chat session. It can be created
 * manually or via the [io.kory.core.dsl.chat.koryChat] DSL builder.
 *
 * @property messages The ordered list of messages in the conversation.
 * @property model The identifier of the model to use (e.g. `"gpt-5.6-sol"`).
 *
 * @sample examples.core.chat.basicChatCreationWithDsl
 * @sample examples.core.chat.basicChatCreationWithoutDsl
 */
@Serializable
data class Chat(
    val messages: List<Message>,
    val model: String,
) {
    /**
     * Converts this chat into a [ChatRequest] with optional generation parameters.
     *
     * @param temperature Sampling temperature (0.0–2.0). `null` uses provider default.
     * @param maxTokens Maximum number of tokens to generate. `null` uses provider default.
     * @param topK Top-K sampling parameter. `null` uses provider default.
     * @param reasoning Reasoning/thinking configuration. `null` disables reasoning.
     * @param tools List of [KoryTool] instances the model may call.
     * @return A [ChatRequest] ready to be sent via a [io.kory.core.chat.client.ChatClient].
     *
     * @see io.kory.core.chat.Chat
     * @see io.kory.core.dsl.chat.koryChat
     * @see io.kory.core.tool.KoryTool
     *
     * @sample examples.core.extensions.asChatRequestFunction
     */
    fun asChatRequest(temperature: Double? = null,
                      maxTokens: Int? = null, topK: Int? = null,
                      reasoning: ReasoningConfig? = null,
                      tools: List<KoryTool<*, *>> = emptyList()
    ): ChatRequest = ChatRequest(
        chat = this,
        temperature = temperature,
        maxTokens = maxTokens,
        topK = topK,
        reasoning = reasoning,
        tools = tools
    )

    /**
     * Converts this chat into a [ChatRequest] with tools defined via DSL.
     *
     * @param temperature Sampling temperature (0.0–2.0). `null` uses provider default.
     * @param maxTokens Maximum number of tokens to generate. `null` uses provider default.
     * @param topK Top-K sampling parameter. `null` uses provider default.
     * @param reasoning Reasoning/thinking configuration. `null` disables reasoning.
     * @param tools A [ToolsBuilder] lambda for registering [KoryTool] instances.
     * @return A [ChatRequest] ready to be sent via a [io.kory.core.chat.client.ChatClient].
     *
     * @see io.kory.core.chat.Chat
     * @see io.kory.core.dsl.chat.koryChat
     * @see io.kory.core.tool.KoryTool
     *
     * @sample examples.core.extensions.asChatRequestFunction
     */
    fun asChatRequest(temperature: Double? = null,
                      maxTokens: Int? = null, topK: Int? = null,
                      reasoning: ReasoningConfig? = null,
                      tools: ToolsBuilder.() -> Unit
    ): ChatRequest = ChatRequest(
        chat = this,
        temperature = temperature,
        maxTokens = maxTokens,
        topK = topK,
        reasoning = reasoning,
        tools = koryTools(tools)
    )
}
