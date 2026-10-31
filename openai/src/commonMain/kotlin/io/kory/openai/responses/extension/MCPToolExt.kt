package io.kory.openai.responses.extension

import io.kory.core.mcp.tool.MCPTool
import io.kory.openai.responses.io.tool.OpenAIMcpTool
import io.kory.openai.responses.io.tool.OpenAIResponsesToolDto
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

fun MCPTool.toOpenAIResponsesDto(
    requireApproval: JsonElement? = JsonPrimitive("never"),
    serverDescription: String? = null,
    authorization: String? = null,
): OpenAIResponsesToolDto.McpTool = OpenAIResponsesToolDto.McpTool(
    serverLabel = serverLabel,
    serverUrl = url,
    serverDescription = serverDescription,
    headers = headers.takeIf { it.isNotEmpty() },
    allowedTools = allowedTools?.toList(),
    requireApproval = requireApproval,
    authorization = authorization,
)

fun MCPTool.toOpenAIMcpTool(
    requireApproval: JsonElement? = JsonPrimitive("never"),
    serverDescription: String? = null,
    authorization: String? = null,
): OpenAIMcpTool = OpenAIMcpTool(
    serverLabel = serverLabel,
    serverUrl = url,
    serverDescription = serverDescription,
    headers = headers.takeIf { it.isNotEmpty() },
    allowedTools = allowedTools?.toList(),
    requireApproval = requireApproval,
    authorization = authorization,
)