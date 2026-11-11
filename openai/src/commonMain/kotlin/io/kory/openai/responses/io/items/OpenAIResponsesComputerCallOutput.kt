package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAIResponseStatus
import io.kory.openai.responses.model.computer.OpenAIResponsesPendingSafetyCheck
import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The result of a computer tool call.
 *
 * @property id The unique ID of the computer call output.
 * @property callId The ID of the tool call that produced this output.
 * @property output The result of the tool call: a string or a screenshot.
 * @property acknowledgedSafetyChecks The safety checks performed and acknowledged before this output.
 * `null` when not reported.
 * @property status The status of the input item. Populated when items are returned via API. `null` otherwise.
 */
@Serializable
@SerialName("computer_call_output")
data class OpenAIResponsesComputerCallOutput(
    val id: String? = null,
    @SerialName("call_id") val callId: String,
    val output: OpenAIResponsesComputerCallResult,
    @SerialName("acknowledged_safety_checks") val acknowledgedSafetyChecks: List<OpenAIResponsesPendingSafetyCheck>? = null,
    val status: OpenAIStatus? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Computer Call Output, call_id: $callId"
}
