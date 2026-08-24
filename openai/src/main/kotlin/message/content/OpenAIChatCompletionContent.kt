package io.kory.openai.message.content

import io.kory.core.message.content.Content
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.serializer.OpenAIChatCompletionContentSerializer
import kotlinx.serialization.Serializable

/**
 * OpenAI content type that serializes as either a plain string or a JSON array.
 *
 * Uses [OpenAIChatCompletionContentSerializer] for polymorphic serialization:
 * - [Text] serializes as a JSON string (e.g. `"hello"`).
 * - [Parts] serializes as a JSON array (e.g. `[{"type":"text","text":"hello"}]`).
 */
@Serializable(with = OpenAIChatCompletionContentSerializer::class)
sealed interface OpenAIChatCompletionContent {
    /**
     * Converts this to a core [Content.Response].
     *
     * @return The mapped response content.
     */
    fun toResponseContent(): Content.Response

    /**
     * Converts this to a core [Content.Request].
     *
     * @return The mapped request content.
     */
    fun toRequestContent(): Content.Request

    /**
     * Plain text content.
     *
     * @property value The text string.
     */
    data class Text(
        val value: String
    ) : OpenAIChatCompletionContent, Mapper<Content.Text> {
        override fun map(): Content.Text = Content.Text(value)
        override fun toRequestContent(): Content.Request = map()
        override fun toResponseContent(): Content.Response = map()
    }

    /**
     * Multi-part content (e.g. text + images).
     *
     * @property parts The list of content parts.
     */
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