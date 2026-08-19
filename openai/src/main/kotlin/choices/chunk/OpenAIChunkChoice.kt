package io.kory.openai.choices.chunk

import io.kory.core.chat.choice.ChatChoice
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
) : Mapper<ChatChunkChoice> {
    override fun map(): ChatChunkChoice {
        delta.effectiveReasoning?.let { effectiveReasoning ->
            return ChatChunkChoice(
                index = index,
                content = Content.Reasoning(effectiveReasoning),
                finishReason = finishReason
            )
        }

        return ChatChunkChoice(
            index = index,
            content = Content.Text(this.delta.content.orEmpty()),
            finishReason = finishReason
        )
    }

    fun toChatChunkChoice(): ChatChunkChoice = map()
}
