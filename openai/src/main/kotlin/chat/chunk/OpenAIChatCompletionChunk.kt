package io.kory.openai.chat.chunk

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.choices.chunk.OpenAIChunkChoice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionChunk(
    val id: String,
    @SerialName("object") val obj: String,
    val created: Long,
    val model: String,
    val choices: List<OpenAIChunkChoice>
) : Mapper<ChatChunk> {
    override fun map(): ChatChunk {
        return ChatChunk(
            choices = choices.map { it.toChatChoice() },
        )
    }

    fun toChatChunk(): ChatChunk = map()

}
