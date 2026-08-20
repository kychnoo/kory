package io.kory.openai.choices.chunk

import io.kory.core.chat.choice.ChatChunkChoice
import io.kory.core.message.content.Content
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.chat.chunk.OpenAIDelta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    fun toChatChunkChoices(): List<ChatChunkChoice> = map()
    fun toChatChunkChoice(): ChatChunkChoice = map().first()
}
