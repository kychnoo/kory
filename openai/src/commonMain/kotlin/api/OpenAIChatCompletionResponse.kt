package io.kory.openai.api

import io.kory.core.chat.response.ChatResponse
import io.kory.core.utils.mapper.Mapper
import io.kory.core.utils.mapper.mapDomain
import io.kory.openai.choices.OpenAIChoice
import io.kory.openai.usages.OpenAIUsage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Raw OpenAI chat completion response.
 *
 * Contains the full response from the `/chat/completions` endpoint, including
 * choices, usage statistics, and metadata. Use [toChatResponse] to convert
 * to the provider-agnostic [ChatResponse].
 *
 * @property id Unique identifier for this completion.
 * @property obj Object type (always `"chat.completion"`).
 * @property created Epoch timestamp when the completion was created.
 * @property model The model used for this completion.
 * @property choices The list of completion choices.
 * @property usage Token usage statistics. `null` if not included.
 */
@Serializable
data class OpenAIChatCompletionResponse(
    val id: String,
    @SerialName("object") val obj: String,
    val created: Long,
    val model: String,
    val choices: List<OpenAIChoice>,
    val usage: OpenAIUsage? = null,
) : Mapper<ChatResponse> {
    override fun map(): ChatResponse = ChatResponse(
        choices = choices.mapDomain()
    )

    /**
     * Converts this response to a provider-agnostic [ChatResponse].
     *
     * @return A [ChatResponse] with mapped choices.
     */
    fun toChatResponse(): ChatResponse = map()
}