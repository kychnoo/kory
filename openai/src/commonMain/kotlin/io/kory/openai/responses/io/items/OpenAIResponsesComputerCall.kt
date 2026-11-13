package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.computer.OpenAIResponsesComputerAction
import io.kory.openai.responses.model.computer.OpenAIResponsesPendingSafetyCheck
import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("computer_call")
data class OpenAIResponsesComputerCall(
    val id: String,
    @SerialName("call_id") val callId: String,
    @SerialName("pending_safety_checks") val pendingSafetyChecks: List<OpenAIResponsesPendingSafetyCheck>,
    val status: OpenAIStatus? = null,
    val action: OpenAIResponsesComputerAction? = null,
    val actions: List<OpenAIResponsesComputerAction> = emptyList(),
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Computer Call: $id, status: $status"
}