package io.kory.core.dsl.chat.request

import io.kory.core.chat.Chat
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.chat.request.ChatRequest
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.koryChat
import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.dsl.tool.ToolsBuilder
import io.kory.core.dsl.tool.koryTools
import io.kory.core.tool.KoryTool

/**
 * DSL builder for constructing a [ChatRequest] with full configuration.
 *
 * Use [koryChatRequest] as the entry point. Configure the chat content via [chat],
 * and optionally set [temperature], [maxTokens], [topK], [reasoning], and tools
 * via [registerTools].
 *
 * @sample examples.core.chat.request.chatRequestCreationWithDsl
 */
@KoryDsl
class ChatRequestBuilder {
    val choicesCount: Int? = null
    /** Sampling temperature (0.0–2.0). `null` uses provider default. */
    var temperature: Double? = null
    /** Maximum tokens to generate. `null` uses provider default. */
    var maxTokens: Int? = null
    /** Top-K sampling parameter. `null` uses provider default. */
    var topK: Int? = null
    /** Reasoning/thinking configuration. `null` disables reasoning. */
    var reasoning: ReasoningConfig? = null

    private var chat: Chat? = null
    private val toolsList = mutableListOf<KoryTool<*, *>>()

    /**
     * Sets the chat content using DSL-style message construction.
     *
     * @param model The model identifier.
     * @param blocks A [ChatBuilder] lambda for adding messages.
     */
    fun chat(model: String, blocks: ChatBuilder.() -> Unit) {
        this.chat = koryChat(model, blocks)
    }

    /**
     * Sets the chat content from an existing [Chat] instance.
     *
     * @param chat The chat to use.
     */
    fun chat(chat: Chat) {
        this.chat = chat
    }

    /**
     * Registers one or more tools.
     *
     * @param tools The tools to register. Duplicates are ignored.
     */
    fun registerTools(vararg tools: KoryTool<*, *>) {
        registerTools(tools.toList())
    }

    /**
     * Registers collection of [KoryTool].
     *
     * @param tools The tools collection to register. Duplicates are ignored.
     */
    fun registerTools(tools: Collection<KoryTool<*, *>>) {
        for (tool in tools) {
            if (tool !in toolsList) {
                toolsList.add(tool)
            }
        }
    }

    /**
     * Registers tools using a [ToolsBuilder] DSL.
     *
     * @param block A [ToolsBuilder] lambda for registering tools.
     */
    fun registerTools(block: ToolsBuilder.() -> Unit) {
        registerTools(koryTools(block))
    }

    internal fun build(): ChatRequest {
        val currentChat = requireNotNull(chat) { "Chat must be initialized in ChatRequestBuilder" }
        return ChatRequest(
            chat = currentChat,
            choicesCount = choicesCount,
            temperature = temperature,
            maxTokens = maxTokens,
            topK = topK,
            tools = toolsList,
            reasoning = reasoning,
        )
    }
}

/**
 * Creates a [ChatRequest] using the DSL builder.
 *
 * @param blocks A [ChatRequestBuilder] lambda for configuring the request.
 * @return A new [ChatRequest] instance.
 * @throws IllegalStateException if `chat {}` was not called inside the builder.
 *
 * @sample examples.core.chat.request.chatRequestCreationWithDsl
 */
fun koryChatRequest(blocks: ChatRequestBuilder.() -> Unit): ChatRequest {
    return ChatRequestBuilder().apply(blocks).build()
}