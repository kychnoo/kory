package io.kory.openai.responses.io.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The user's response to an [OpenAIResponsesMcpApprovalRequest]: whether the tool
 * invocation was approved, with an optional explanation.
 *
 * @property id The unique ID of the approval response.
 * @property approvalRequestId The ID of the approval request being answered.
 * @property approve Whether the model was approved to invoke the tool.
 * @property reason Optional explanation for the decision.
 */
@Serializable
@SerialName("mcp_approval_response")
data class OpenAIResponsesMcpApprovalResponse(
    val id: String? = null,
    @SerialName("approval_request_id") val approvalRequestId: String,
    val approve: Boolean,
    val reason: String? = null,
) : OpenAIResponseInputItem
