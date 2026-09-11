package io.kory.openai.data.local.modality

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class OpenAIModality(val value: String) {
    companion object {
        val Text = OpenAIModality("text")
        val Audio = OpenAIModality("audio")
    }

    fun equalsIgnoreCase(other: String) = value.equals(other, ignoreCase = true)
}