package io.kory.openai.message.content

import io.kory.core.message.content.Content
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.serializer.OpenAIChatCompletionContentSerializer
import kotlinx.serialization.Serializable

@Serializable(with = OpenAIChatCompletionContentSerializer::class)
sealed interface OpenAIChatCompletionContent {
    fun toResponseContent(): Content.Response
    fun toRequestContent(): Content.Request

    data class Text(
        val value: String
    ) : OpenAIChatCompletionContent, Mapper<Content.Text> {
        override fun map(): Content.Text = Content.Text(value)
        override fun toRequestContent(): Content.Request = map()
        override fun toResponseContent(): Content.Response = map()
    }

    data class Parts(
        val parts: List<OpenAIChatCompletionContentPart>
    ) : OpenAIChatCompletionContent, Mapper<Content.Parts> {
        override fun map(): Content.Parts = Content.Parts(
            parts = parts.map { it.toContentPart() }
        )

        override fun toRequestContent(): Content.Request = map()
        override fun toResponseContent(): Content.Response = map()
    }
}