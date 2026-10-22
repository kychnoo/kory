package io.kory.openai.responses.io.items

import io.kory.openai.responses.io.items.function.OpenAIFunctionCallOutputOutput
import io.kory.openai.responses.model.OpenAIResponsesCaller
import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("function_call_output")
data class OpenAIResponsesFunctionCallOutput(
    val id: String? = null,
    val output: OpenAIFunctionCallOutputOutput,
    val status: OpenAIStatus? = null,
    @SerialName("call_id") val callId: String? = null,
    val caller: OpenAIResponsesCaller? = null,
    @SerialName("created_by") val createdBy: String? = null,
    val name: String? = null,
    val namespace: String? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Function Call Output, name: $name"
}
