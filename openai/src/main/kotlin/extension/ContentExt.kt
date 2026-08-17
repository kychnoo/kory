package io.kory.openai.extension

import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.content.OpenAIChatCompletionContentPart
import io.kory.openai.message.content.OpenAIImageUrl

fun Content.Request.toOpenAIContent(): OpenAIChatCompletionContent = when (this) {
    is Content.Text -> OpenAIChatCompletionContent.Text(text)
    is Content.Parts -> OpenAIChatCompletionContent.Parts(parts.toOpenAIContentParts())
}

fun ContentPart.toOpenAIContentPart(): OpenAIChatCompletionContentPart = when (this) {
    is ContentPart.Text -> OpenAIChatCompletionContentPart.Text(value)
    is ContentPart.Image -> OpenAIChatCompletionContentPart.Image(imageUrl = OpenAIImageUrl.fromImageSource(source))
}

fun List<ContentPart>.toOpenAIContentParts(): List<OpenAIChatCompletionContentPart> = this.map { it.toOpenAIContentPart() }