package io.kory.openai.responses.dsl

import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.responses.dto.OpenAIResponsesRequest
import io.kory.openai.responses.input.OpenAIInput
import io.kory.openai.responses.input.OpenAIInputItem

@OpenAIRequestDsl
class OpenAIResponsesRequestBuilder {
    internal var model: String? = null
    private var selectedInput: OpenAIInput? = null

    fun inputText(text: String) {
        selectedInput = OpenAIInput.Text(text)
    }

    fun inputItems(items: List<OpenAIInputItem>) {
        selectedInput = OpenAIInput.InputItemList(items)
    }

    fun inputItems(vararg items: OpenAIInputItem) {
        inputItems(items.toList())
    }

    fun inputItems(block: OpenAIResponseInputItemBuilder.() -> Unit) {
        inputItems(openAIResponseInputItems(block))
    }

    internal fun build(): OpenAIResponsesRequest {
        val lastInput = selectedInput
        val sModel = model

        requireNotNull(sModel) { "model cannot be null" }
        requireNotNull(lastInput) { "selectedInput cannot be null" }

        return OpenAIResponsesRequest(
            model = sModel,
            input = lastInput
        )
    }
}

fun openAIResponsesRequest(model: String, block: OpenAIResponsesRequestBuilder.() -> Unit): OpenAIResponsesRequest {
    return OpenAIResponsesRequestBuilder().apply {
        this.model = model
        block()
    }.build()
}