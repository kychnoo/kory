package io.kory.openai.chat.chunk

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.choices.chunk.OpenAIChunkChoice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A streaming SSE chunk from the OpenAI chat completion API.
 *
 * @property id Unique identifier for this completion.
 * @property obj Object type (always `"chat.completion.chunk"`).
 * @property created Epoch timestamp.
 * @property model The model used.
 * @property choices The streaming choices in this chunk.
 */
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
            choices = choices.flatMap { it.toChatChunkChoices() },
        )
    }

    /**
     * Converts this chunk to a core [ChatChunk].
     *
     * @return A [ChatChunk] with mapped choices.
     */
    fun toChatChunk(): ChatChunk = map()

}
