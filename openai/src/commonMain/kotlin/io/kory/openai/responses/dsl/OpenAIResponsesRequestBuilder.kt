package io.kory.openai.responses.dsl

import io.kory.core.exception.messages.MessageRequiredException
import io.kory.core.exception.model.ModelRequiredException
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.responses.dto.OpenAIResponsesRequest
import io.kory.openai.responses.io.OpenAIResponseInput
import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.shared.param.OpenAIMetadata

@OpenAIRequestDsl
class OpenAIResponsesRequestBuilder {
    internal var model: String? = null
    private var selectedInput: OpenAIResponseInput? = null
    private var selectedInstructions: String? = null

    var background: Boolean = false
    var maxOutputTokens: Int? = null
    var maxToolCalls: Int? = null
    var metadata: OpenAIMetadata? = null

    fun inputText(text: String) {
        selectedInput = OpenAIResponseInput.Text(text)
    }

    fun inputItems(items: List<OpenAIResponseInputItem>) {
        selectedInput = OpenAIResponseInput.InputItemList(items)
    }

    fun inputItems(vararg items: OpenAIResponseInputItem) {
        inputItems(items.toList())
    }

    fun inputItems(block: OpenAIResponseInputItemBuilder.() -> Unit) {
        inputItems(openAIResponseInputItems(block))
    }

    fun setInstructions(instructions: String) {
        if (instructions.isNotBlank()) {
            selectedInstructions = instructions
        }
    }

    internal fun build(): OpenAIResponsesRequest {
        val lastInput = selectedInput
        val sModel = model

        if (sModel.isNullOrBlank()) {
            throw ModelRequiredException("The model must be included in the request")
        }

        if (lastInput == null || lastInput.isEmpty()) {
            throw MessageRequiredException("Messages must be included in the request")
        }

        return OpenAIResponsesRequest(
            model = sModel,
            input = lastInput,
            instructions = selectedInstructions,
            maxOutputTokens = maxOutputTokens,
            maxToolCalls = maxToolCalls,
            metadata = metadata,
            background = background
        )
    }
}

fun openAIResponsesRequest(model: String, block: OpenAIResponsesRequestBuilder.() -> Unit): OpenAIResponsesRequest {
    return OpenAIResponsesRequestBuilder().apply {
        this.model = model
        block()
    }.build()
}