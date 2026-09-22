package io.kory.openai.responses.message.content

import io.kory.openai.responses.serialization.OpenAIResponsesContentSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable(with = OpenAIResponsesContentSerializer::class)
sealed interface OpenAIResponseContent {
    @Serializable
    @JvmInline
    value class Text(val value: String) : OpenAIResponseContent

    @Serializable
    @JvmInline
    value class ResponseInputMessageContentList(val list: List<OpenAIResponseInputContent>) : OpenAIResponseContent
}