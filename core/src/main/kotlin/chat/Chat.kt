package io.kory.core.chat

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.chat.request.ChatRequest
import io.kory.core.dsl.tool.ToolsBuilder
import io.kory.core.dsl.tool.koryTools
import io.kory.core.message.Message
import io.kory.core.model.Model
import io.kory.core.tool.KoryTool
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val messages: List<Message>,
    val model: String,
) {
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
