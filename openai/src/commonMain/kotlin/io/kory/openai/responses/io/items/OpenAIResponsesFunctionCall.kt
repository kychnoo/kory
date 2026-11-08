package io.kory.openai.responses.io.items

import io.kory.openai.responses.io.items.function.OpenAIFunctionCallOutputOutput
import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("function_call")
data class OpenAIResponsesFunctionCall(
    val arguments: String,
    @SerialName("call_id") val callId: String,
    val name: String,
    val id: String? = null,
    val async: Boolean? = null,
    val namespace: String? = null,
    val status: OpenAIStatus? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Function Call, name: $name"

    fun toFunctionCallOutput(output: OpenAIFunctionCallOutputOutput): OpenAIResponsesFunctionCallOutput
        = OpenAIResponsesFunctionCallOutput(
            callId = callId,
            name = name,
            id = id,
            status = status,
            namespace = namespace,
            output = output,
        )

}