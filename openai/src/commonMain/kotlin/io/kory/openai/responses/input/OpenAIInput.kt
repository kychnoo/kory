package io.kory.openai.responses.input

import io.kory.openai.responses.serialization.OpenAIInputSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable(with = OpenAIInputSerializer::class)
sealed interface OpenAIInput {
    @Serializable
    @JvmInline
    value class Text(val value: String) : OpenAIInput

   @Serializable
   @JvmInline
   value class InputItemList(val items: List<OpenAIInputItem>) : OpenAIInput
}