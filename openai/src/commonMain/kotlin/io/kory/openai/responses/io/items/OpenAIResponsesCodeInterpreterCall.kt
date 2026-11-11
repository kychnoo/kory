package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAICodeInterpreterStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A code interpreter tool call: the model writes and runs code in a container.
 *
 * @property id The unique ID of the code interpreter call.
 * @property status The status of the code interpreter call.
 * @property containerId The container the code was executed in.
 * @property code The code that the model executed.
 * @property outputs The outputs produced by the interpreter (logs and/or images).
 */
@Serializable
@SerialName("code_interpreter_call")
data class OpenAIResponsesCodeInterpreterCall(
    val id: String,
    val status: OpenAICodeInterpreterStatus,
    @SerialName("container_id") val containerId: String,
    val code: String? = null,
    val outputs: List<OpenAIResponsesCodeInterpreterOutput>? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Code interpreter call: $id, status: $status"
}
