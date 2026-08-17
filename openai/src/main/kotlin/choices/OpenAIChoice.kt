package io.kory.openai.choices

import io.kory.core.chat.choice.ChatChoice
import io.kory.core.message.content.Content
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.message.OpenAIMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChoice(
    val index: Int,
    val message: OpenAIMessage,
    @SerialName("finish_reason")
    val finishReason: String? = null,
) : Mapper<ChatChoice> {
    override fun map(): ChatChoice = ChatChoice(
        content = message.content.toResponseContent()
    )

    fun toChatChoice(): ChatChoice = map()
}
