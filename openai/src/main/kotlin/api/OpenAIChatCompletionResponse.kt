package io.kory.openai.api

import io.kory.core.chat.response.ChatResponse
import io.kory.core.utils.mapper.Mapper
import io.kory.core.utils.mapper.mapDomain
import io.kory.openai.choices.OpenAIChoice
import io.kory.openai.usages.OpenAIUsage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionResponse(
    val id: String,
    @SerialName("object") val obj: String,
    val created: Long,
    val model: String,
    val choices: List<OpenAIChoice>,
    val usage: OpenAIUsage? = null,
) : Mapper<ChatResponse> {
    override fun map(): ChatResponse {
        return ChatResponse(
            choices = choices.mapDomain()
        )
    }

    fun toChatResponse(): ChatResponse = map()
}