package io.kory.openai.responses.io.items

import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The result of a local shell tool call, sent back to the model as input.
 *
 * @property id The unique ID of the local shell call output.
 * @property callId The ID of the tool call this output answers.
 * @property output The captured output of the executed shell command.
 * @property status The status of the tool call. `null` when not reported.
 */
@Serializable
@SerialName("local_shell_call_output")
data class OpenAIResponsesLocalShellCallOutput(
    val id: String? = null,
    @SerialName("call_id") val callId: String,
    val output: String,
    val status: OpenAIStatus? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Local shell call output, call_id: $callId"
}
