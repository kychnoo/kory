package io.kory.core.chat.chunk

import io.kory.core.chat.choice.ChatChunkChoice
import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

/**
 * A streaming response chunk from an LLM provider.
 *
 * Each chunk contains one or more [ChatChunkChoice] objects with incremental content.
 * Collect chunks from [ChatClient.chatStream][io.kory.core.chat.ChatClient.chatStream]
 * to build up the full response.
 *
 * @property choices The streaming choices in this chunk.
 *
 * @sample examples.core.chat.chunk.processChatChunk
 */
@Serializable
data class ChatChunk(
    val choices: List<ChatChunkChoice>,
)
