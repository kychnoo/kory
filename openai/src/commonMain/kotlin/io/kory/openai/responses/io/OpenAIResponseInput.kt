package io.kory.openai.responses.io

import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.serialization.OpenAIInputSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable(with = OpenAIInputSerializer::class)
sealed interface OpenAIResponseInput {
    fun isEmpty(): Boolean

    @Serializable
    @JvmInline
    value class Text(val value: String) : OpenAIResponseInput {
        override fun isEmpty(): Boolean = value.isBlank()
    }

   @Serializable
   @JvmInline
   value class InputItemList(val items: List<OpenAIResponseInputItem>) : OpenAIResponseInput {
       override fun isEmpty(): Boolean = items.isEmpty()
   }
}