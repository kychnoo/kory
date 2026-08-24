package io.kory.core.chat.choice

import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

/**
 * A single choice within a streaming [io.kory.core.chat.chunk.ChatChunk].
 *
 * Unlike [ChatChoice] which holds a complete list of contents, this holds a single
 * incremental [Content.StreamResponse] item — either a [Content.Text] fragment,
 * [Content.Reasoning] fragment, or [Content.ToolCallDelta].
 *
 * @property index The index of this choice (0-based).
 * @property content The incremental content chunk for this choice.
 * @property finishReason Why the model stopped. `null` while still streaming.
 *
 * @see io.kory.core.chat.chunk.ChatChunk
 * @see io.kory.core.message.content.Content
 *
 * @sample examples.core.chat.choice.processChunkChoice
 */
@Serializable
data class ChatChunkChoice(
    val index: Int,
    val content: Content.StreamResponse,
    val finishReason: String? = null,
)