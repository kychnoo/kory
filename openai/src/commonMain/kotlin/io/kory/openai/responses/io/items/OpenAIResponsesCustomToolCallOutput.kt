package io.kory.openai.responses.io.items

import io.kory.openai.responses.io.items.function.OpenAIFunctionCallOutputOutput
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The result of a custom tool call, sent back to the model as input.
 *
 * @property id The unique ID of the custom tool call output.
 * @property callId The ID of the tool call this output answers.
 * @property output The output of the tool call.
 */
@Serializable
@SerialName("custom_tool_call_output")
data class OpenAIResponsesCustomToolCallOutput(
    val id: String? = null,
    @SerialName("call_id") val callId: String,
    val output: OpenAIFunctionCallOutputOutput,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Custom tool call output, call_id: $callId"
}
