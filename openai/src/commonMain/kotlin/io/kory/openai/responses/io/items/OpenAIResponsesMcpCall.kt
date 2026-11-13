package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAIMcpCallStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * An MCP tool call performed by the model against a configured MCP server.
 *
 * @property id The unique ID of the MCP tool call.
 * @property serverLabel The label of the MCP server the tool was invoked on.
 * @property name The name of the tool that was invoked.
 * @property arguments The JSON-encoded arguments passed to the tool.
 * @property output The output of the tool call. `null` when the call has not completed.
 * @property error The error from the tool call, when it failed. `null` on success.
 * @property status The status of the MCP tool call. `null` when not reported.
 * @property approvalRequestId The ID of the corresponding approval request when the call
 * requires user approval. `null` otherwise.
 */
@Serializable
@SerialName("mcp_call")
data class OpenAIResponsesMcpCall(
    val id: String,
    @SerialName("server_label") val serverLabel: String,
    val name: String,
    val arguments: String,
    val output: String? = null,
    val error: JsonElement? = null,
    val status: OpenAIMcpCallStatus? = null,
    @SerialName("approval_request_id") val approvalRequestId: String? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "MCP call: $name on $serverLabel, status: $status"
}
