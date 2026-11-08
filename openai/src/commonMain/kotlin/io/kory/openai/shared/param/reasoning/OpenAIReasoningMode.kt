package io.kory.openai.shared.param.reasoning

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Reasoning mode for the OpenAI Responses API.
 *
 * Use the predefined constants, or instantiate with a custom value
 * for non-standard cases.
 *
 * @property value The mode string sent to the API.
 */
@Serializable
@JvmInline
value class OpenAIReasoningMode(val value: String) {
    companion object {
        /** Standard reasoning mode. */
        val Standart = OpenAIReasoningMode("standart")
        /** Pro reasoning mode. */
        val Pro = OpenAIReasoningMode("pro")
    }
}