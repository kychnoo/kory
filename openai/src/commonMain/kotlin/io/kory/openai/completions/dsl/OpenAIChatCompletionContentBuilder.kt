package io.kory.openai.completions.dsl

import io.kory.openai.completions.message.content.OpenAIChatCompletionContent
import io.kory.openai.completions.message.content.OpenAIChatCompletionContentPart
import io.kory.openai.completions.message.content.image.OpenAIImageUrl

@OpenAIRequestDsl
class OpenAIChatCompletionContentBuilder internal constructor() {
    val parts = mutableListOf<OpenAIChatCompletionContentPart>()

    fun text(value: String) {
        parts.add(OpenAIChatCompletionContentPart.Text(value))
    }

    fun image(imageUrl: OpenAIImageUrl) {
        parts.add(OpenAIChatCompletionContentPart.Image(imageUrl))
    }

    internal fun build(): OpenAIChatCompletionContent {
        return if (parts.size == 1 && parts.first() is OpenAIChatCompletionContentPart.Text)
            OpenAIChatCompletionContent.Text((parts.first() as OpenAIChatCompletionContentPart.Text).text)
        else OpenAIChatCompletionContent.Parts(parts)
    }
}

fun openAIChatCompletionContent(block: OpenAIChatCompletionContentBuilder.() -> Unit): OpenAIChatCompletionContent {
    return OpenAIChatCompletionContentBuilder().apply(block).build()
}