package io.kory.openai.extension

import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.content.OpenAIChatCompletionContentPart
import io.kory.openai.message.content.OpenAIImageUrl

/**
 * Converts a core [Content.Request] to an OpenAI content type.
 *
 * @return The corresponding [OpenAIChatCompletionContent], or `null` for
 *   [Content.ToolResult] and [Content.ToolCall] (which have no OpenAI equivalent as content).
 */
fun Content.Request.toOpenAIContent(): OpenAIChatCompletionContent? = when (this) {
    is Content.Text -> OpenAIChatCompletionContent.Text(text)
    is Content.Parts -> OpenAIChatCompletionContent.Parts(parts.toOpenAIContentParts())
    is Content.ToolResult, is Content.ToolCall -> null
}

/**
 * Converts a core [ContentPart] to an OpenAI content part.
 *
 * @return The corresponding [OpenAIChatCompletionContentPart].
 */
fun ContentPart.toOpenAIContentPart(): OpenAIChatCompletionContentPart = when (this) {
    is ContentPart.Text -> OpenAIChatCompletionContentPart.Text(value)
    is ContentPart.Image -> OpenAIChatCompletionContentPart.Image(imageUrl = OpenAIImageUrl.fromImageSource(source))
}

/**
 * Converts a list of core [ContentPart] items to OpenAI content parts.
 *
 * @return A list of [OpenAIChatCompletionContentPart].
 */
fun List<ContentPart>.toOpenAIContentParts(): List<OpenAIChatCompletionContentPart> = this.map { it.toOpenAIContentPart() }