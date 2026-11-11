package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAIResponsesCaller
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A custom tool call: a tool whose input is freeform text defined by the caller.
 *
 * @property id The unique ID of the custom tool call.
 * @property callId The ID of the tool call, used to route the output back to the model.
 * @property name The name of the tool that was invoked.
 * @property input The raw input the model produced for the tool.
 * @property namespace The namespace of the tool, when the tool is namespaced.
 * @property caller Who triggered the tool call.
 * @property async Whether the tool call runs asynchronously.
 */
@Serializable
@SerialName("custom_tool_call")
data class OpenAIResponsesCustomToolCall(
    val id: String? = null,
    @SerialName("call_id") val callId: String,
    val name: String,
    val input: String,
    val namespace: String? = null,
    val caller: OpenAIResponsesCaller? = null,
    val async: Boolean? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Custom tool call: $name"
}
