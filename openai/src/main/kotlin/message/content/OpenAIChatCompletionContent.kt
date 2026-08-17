package io.kory.openai.message.content

import io.kory.core.message.content.Content
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.serializer.OpenAIChatCompletionContentSerializer
import kotlinx.serialization.Serializable

@Serializable(with = OpenAIChatCompletionContentSerializer::class)
sealed interface OpenAIChatCompletionContent {
    fun toContent(): Content

    data class Text(
        val value: String
    ) : OpenAIChatCompletionContent, Mapper<Content> {
        override fun map(): Content = Content.Text(value)
        override fun toContent(): Content = map()
    }

    data class Parts(
        val parts: List<OpenAIChatCompletionContentPart>
    ) : OpenAIChatCompletionContent, Mapper<Content> {
        override fun map(): Content = Content.Parts(
            parts = parts.map { it.toContentPart() }
        )
        override fun toContent(): Content = map()
    }
}