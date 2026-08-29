package io.kory.core.chat.choice

import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

/**
 * A single completion choice within a [io.kory.core.chat.response.ChatResponse].
 *
 * Each choice contains an ordered list of content items (text, reasoning, tool calls, etc.)
 * and an optional finish reason indicating why the model stopped generating.
 *
 * @property index The index of this choice (0-based).
 * @property contents The content items produced by the model for this choice.
 * @property finishReason Why the model stopped: `"stop"`, `"length"`, `"tool_calls"`, etc. `null` if not provided.
 *
 * @see io.kory.core.message.content.Content
 *
 * @sample examples.core.chat.choice.processChatChoice
 */
@Serializable
data class ChatChoice(
    val index: Int,
    val contents: List<Content.Response>,
    val finishReason: String? = null,
) {
    /**
     * Returns the first content item in this choice, or `null` if the choice is empty.
     */
    val firstContent: Content.Response? get() = contents.firstOrNull()
}
