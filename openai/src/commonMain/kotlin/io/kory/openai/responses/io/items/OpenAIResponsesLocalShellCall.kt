package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAILocalShellExecAction
import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A local shell tool call: the model requests a shell command execution.
 *
 * @property id The unique ID of the local shell call.
 * @property callId The ID of the tool call, used to route the output back to the model.
 * @property action The shell action (command) to execute.
 * @property status The status of the tool call. `null` when not reported.
 */
@Serializable
@SerialName("local_shell_call")
data class OpenAIResponsesLocalShellCall(
    val id: String,
    @SerialName("call_id") val callId: String,
    val action: OpenAILocalShellExecAction,
    val status: OpenAIStatus? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Local shell call: ${action.command.joinToString(separator = " ")}, status: $status"
}
