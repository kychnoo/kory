package io.kory.openai.responses.io.tool

import io.kory.core.extension.serialization.toJsonSchema
import io.kory.core.tool.BaseTool
import io.kory.core.utils.mapper.DtoMapper
import io.kory.openai.responses.model.OpenAIAllowedCallers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Base interface for OpenAI Responses API tool definitions.
 *
 * @param MT The corresponding DTO type.
 */
sealed interface OpenAIResponsesTool<MT : OpenAIResponsesToolDto> : DtoMapper<MT>

/**
 * OpenAI function tool for the Responses API.
 *
 * @param ARGS The type of the tool's input arguments.
 * @param R The return type of the tool's execution.
 * @property strict Whether to enforce strict schema adherence.
 * @property allowedCallers List of allowed callers, or empty for all.
 * @property async Whether the tool executes asynchronously.
 * @property deferLoading Whether to defer tool loading.
 * @property outputSchema Optional JSON schema for the tool's output.
 *
 * @see OpenAIResponsesTool
 * @see BaseTool
 *
 * @sample io.kory.openai.samples.responses.tool.createToolIUsingAbstractClass
 */
abstract class OpenAIFunctionTool<ARGS, R>(
    override val name: String,
    override val description: String? = null,
    val strict: Boolean? = null,
    val allowedCallers: List<OpenAIAllowedCallers?> = emptyList(),
    val async: Boolean? = null,
    val deferLoading: Boolean? = null,
    val outputSchema: JsonObject? = null
) : OpenAIResponsesTool<OpenAIResponsesToolDto.FunctionTool>, BaseTool<ARGS, R>(name, description) {
    override fun toDto(): OpenAIResponsesToolDto.FunctionTool = OpenAIResponsesToolDto.FunctionTool(
        name = name,
        parameters = argsSerializer.descriptor.toJsonSchema(strict == true),
        description = description,
        strict = strict,
        allowedCallers = allowedCallers,
        async = async,
        deferLoading = deferLoading,
        outputSchema = outputSchema
    )
}

data class OpenAIMcpTool(
    val serverLabel: String,
    val serverUrl: String,
    val serverDescription: String? = null,
    val headers: Map<String, String>? = null,
    val allowedTools: List<String>? = null,
    val requireApproval: JsonElement? = null,
    val authorization: String? = null,
) : OpenAIResponsesTool<OpenAIResponsesToolDto.McpTool> {
    override fun toDto(): OpenAIResponsesToolDto.McpTool = OpenAIResponsesToolDto.McpTool(
        serverLabel = serverLabel,
        serverUrl = serverUrl,
        serverDescription = serverDescription,
        headers = headers,
        allowedTools = allowedTools?.toList(),
        requireApproval = requireApproval,
        authorization = authorization,
    )
}