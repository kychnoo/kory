package io.kory.openai.responses.dsl

import io.kory.core.message.Role
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.responses.input.OpenAIInputItem
import io.kory.openai.responses.message.content.OpenAIResponseContent

@OpenAIRequestDsl
class OpenAIResponseInputItemBuilder {
    private var items = mutableListOf<OpenAIInputItem>()

    fun easyInputMessage(text: String, role: Role) {
        items.add(OpenAIInputItem.EasyInputMessage(
            role = role,
            content = OpenAIResponseContent.Text(text)
        ))
    }

    fun easyInputMessage(role: Role, block: OpenAIResponseContentBuilder.() -> Unit) {
        items.add(OpenAIInputItem.EasyInputMessage(
            role = role,
            content = openAIResponseContent(block)
        ))
    }

    internal fun build(): List<OpenAIInputItem> {
        return items.toList()
    }
}

fun openAIResponseInputItems(block: OpenAIResponseInputItemBuilder.() -> Unit): List<OpenAIInputItem> {
    return OpenAIResponseInputItemBuilder().apply(block).build()
}

