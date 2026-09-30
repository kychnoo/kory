package io.kory.openai.responses.dsl

import io.kory.core.message.Role
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.responses.io.items.EasyInputMessage
import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.message.content.OpenAIResponseContent

@OpenAIRequestDsl
class OpenAIResponseInputItemBuilder {
    private var items = mutableListOf<OpenAIResponseInputItem>()

    fun easyInputMessage(text: String, role: Role) {
        items.add(EasyInputMessage(
            role = role,
            content = OpenAIResponseContent.Text(text)
        ))
    }

    fun easyInputMessage(role: Role, block: OpenAIResponseContentBuilder.() -> Unit) {
        items.add(EasyInputMessage(
                role = role,
                content = openAIResponseContent(block)
            )
        )
    }

    internal fun build(): List<OpenAIResponseInputItem> {
        return items.toList()
    }
}

fun openAIResponseInputItems(block: OpenAIResponseInputItemBuilder.() -> Unit): List<OpenAIResponseInputItem> {
    return OpenAIResponseInputItemBuilder().apply(block).build()
}

