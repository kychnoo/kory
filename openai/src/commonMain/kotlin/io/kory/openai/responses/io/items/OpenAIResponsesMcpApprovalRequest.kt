package io.kory.openai.responses.io.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A request from the model to the user for approval to invoke an MCP tool.
 *
 * @property id The unique ID of the MCP approval request.
 * @property serverLabel The label of the MCP server the tool would be invoked on.
 * @property name The name of the tool that the model wants to invoke.
 * @property arguments The JSON-encoded arguments the model would pass to the tool.
 */
@Serializable
@SerialName("mcp_approval_request")
data class OpenAIResponsesMcpApprovalRequest(
    val id: String,
    @SerialName("server_label") val serverLabel: String,
    val name: String,
    val arguments: String,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "MCP approval request: $name on $serverLabel"
}
