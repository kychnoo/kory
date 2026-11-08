package io.kory.openai.responses.io.items.function

import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.serialization.OpenAIFunctionCallOutputOutputSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable(with = OpenAIFunctionCallOutputOutputSerializer::class)
sealed interface OpenAIFunctionCallOutputOutput {
    @Serializable
    @JvmInline
    value class StringOutput(val value: String) : OpenAIFunctionCallOutputOutput

    @Serializable
    data class OutputContentList(val items: List<OpenAIResponseInputItem>) : OpenAIFunctionCallOutputOutput

}