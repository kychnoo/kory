package io.kory.openai.choices.chunk

import io.kory.core.chat.choice.ChatChunkChoice
import io.kory.core.message.content.Content
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.chat.chunk.OpenAIDelta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A choice within a streaming [OpenAIChatCompletionChunk][io.kory.openai.chat.chunk.OpenAIChatCompletionChunk].
 *
 * Maps to one or more [ChatChunkChoice] items depending on the delta content:
 * - Reasoning deltas produce [Content.Reasoning].
 * - Tool call deltas produce [Content.ToolCallDelta].
 * - Text deltas produce [Content.Text].
 *
 * @property index The index of this choice.
 * @property delta The incremental delta content.
 * @property finishReason Why the model stopped. `null` while still streaming.
 */
@Serializable
data class OpenAIChunkChoice(
    val index: Int,
    val delta: OpenAIDelta,
    @SerialName("finish_reason") val finishReason: String? = null
) : Mapper<List<ChatChunkChoice>> {
    override fun map(): List<ChatChunkChoice> {
        delta.effectiveReasoning?.let { effectiveReasoning ->
            return listOf(ChatChunkChoice(
                index = index,
                content = Content.Reasoning(effectiveReasoning),
                finishReason = finishReason
            ))
        }

        delta.toolCalls?.takeIf { it.isNotEmpty() }?.let { toolCalls ->
            return toolCalls.map { toolCall ->
                ChatChunkChoice(
                    index = index,
                    content = Content.ToolCallDelta(
                        index = toolCall.index,
                        id = toolCall.id,
                        name = toolCall.function.name,
                        argumentsChunk = toolCall.function.arguments
                    )
                )
            }
        }

        return listOf(ChatChunkChoice(
            index = index,
            content = Content.Text(this.delta.content.orEmpty()),
            finishReason = finishReason
        ))
    }

    /**
     * Converts this to a list of core [ChatChunkChoice] items.
     *
     * @return A list of chunk choices (usually one, but multiple for tool call deltas).
     */
    fun toChatChunkChoices(): List<ChatChunkChoice> = map()

    /**
     * Converts this to a single core [ChatChunkChoice].
     *
     * @return The first chunk choice.
     */
    fun toChatChunkChoice(): ChatChunkChoice = map().first()
}
