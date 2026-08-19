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

@KoryDsl
class ChatRequestBuilder {
    var temperature: Double? = null
    var maxTokens: Int? = null
    var topK: Int? = null
    var reasoning: ReasoningConfig? = null

    private var chat: Chat? = null
    private val toolsList = mutableListOf<KoryTool<*, *>>()

    fun chat(model: String, blocks: ChatBuilder.() -> Unit) {
        this.chat = koryChat(model, blocks)
    }

    fun chat(chat: Chat) {
        this.chat = chat
    }

    fun registerTools(vararg tools: KoryTool<*, *>) {
        for (tool in tools) {
            if (tool !in toolsList) {
                toolsList.add(tool)
            }
        }
    }

    fun registerTools(block: ToolsBuilder.() -> Unit) {
        toolsList.addAll(koryTools(block))
    }

    internal fun build(): ChatRequest {
        val currentChat = requireNotNull(chat) { "Chat must be initialized in ChatRequestBuilder" }
        return ChatRequest(
            chat = currentChat,
            temperature = temperature,
            maxTokens = maxTokens,
            topK = topK,
            tools = toolsList,
            reasoning = reasoning,
        )
    }
}

fun koryChatRequest(blocks: ChatRequestBuilder.() -> Unit): ChatRequest {
    return ChatRequestBuilder().apply(blocks).build()
}