package io.kory.core.chat.request

import io.kory.core.chat.Chat
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.tool.KoryTool
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A fully-formed request wrapping a [Chat] with optional generation parameters and tools.
 *
 * Create instances via [io.kory.core.dsl.chat.request.koryChatRequest] or
 * [Chat.asChatRequest][io.kory.core.chat.Chat.asChatRequest].
 *
 * @property chat The conversation containing messages and model name.
 * @property temperature Sampling temperature (0.0–2.0). `null` uses provider default.
 * @property maxTokens Maximum tokens to generate. `null` uses provider default.
 * @property topK Top-K sampling parameter. `null` uses provider default.
 * @property reasoning Reasoning/thinking configuration. `null` disables reasoning.
 * @property tools List of [KoryTool] instances available to the model. Not serialized.
 *
 * @sample examples.core.chat.request.chatRequestCreationWithDsl
 * @see io.kory.core.dsl.chat.request.ChatRequestBuilder
 * @sample examples.core.chat.request.chatRequestCreationWithoutDsl
 *
 * @see io.kory.core.tool.KoryTool
 * @see io.kory.core.dsl.tool.ToolsBuilder
 * @see io.kory.core.chat.reasoning.ReasoningConfig
 */
@Serializable
data class ChatRequest(
    val chat: Chat,
    val choicesCount: Int? = null,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val topK: Int? = null,
    val reasoning: ReasoningConfig? = null,
    @Transient val tools: List<KoryTool<*, *>> = emptyList(),
    private val stream: Boolean = false
)
