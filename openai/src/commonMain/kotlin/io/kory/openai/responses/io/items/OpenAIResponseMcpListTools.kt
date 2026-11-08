package io.kory.openai.responses.io.items

import io.kory.openai.responses.io.tool.OpenAIResponsesMcpToolDefinition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mcp_list_tools")
data class OpenAIResponseMcpListTools(
    val id: String,
    @SerialName("server_label") val serverLabel: String,
    val tools: List<OpenAIResponsesMcpToolDefinition>,
    val error: String? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "mcp list tools: ${serverLabel}. ${tools.size} items"
}
