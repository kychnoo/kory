package io.kory.openai.choices.chunk

import io.kory.core.chat.choice.ChatChoice
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
) : Mapper<ChatChoice> {
    override fun map(): ChatChoice {
        delta.effectiveReasoning?.let { effectiveReasoning ->
            return ChatChoice(Content.Reasoning(effectiveReasoning))
        }

        return ChatChoice(content = Content.Text(this.delta.content.orEmpty()))
    }

    fun toChatChoice(): ChatChoice = map()
}
