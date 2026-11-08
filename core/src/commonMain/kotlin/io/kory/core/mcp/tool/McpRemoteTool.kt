package io.kory.core.mcp.tool

import io.kory.core.mcp.definition.McpToolDefinition
import io.kory.core.mcp.http.McpConnection
import io.kory.core.tool.KoryTool
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class McpRemoteTool internal constructor(
    internal val connection: McpConnection,
    val definition: McpToolDefinition,
    name: String,
) : KoryTool<JsonObject, String>(name, definition.description) {

    override val argsSerializer: KSerializer<JsonObject> = JsonObject.serializer()
    override val resultSerializer: KSerializer<String> = String.serializer()

    /**
     * Relays the call to the MCP server (`tools/call`).
     *
     * @throws io.kory.core.exception.mcp.McpToolExecutionException if the server reported `isError = true`.
     */
    override suspend fun execute(args: JsonObject): String =
        connection.callTool(definition.name, args)

    /**
     * The MCP result is already text - no re-serialization (the default
     * implementation would JSON-quote it).
     */
    override suspend fun executeRaw(argsJson: String, json: Json): String =
        execute(json.parseToJsonElement(argsJson).jsonObject)

    override fun toString(): String = "McpRemoteTool(name=$name, mcpName=${definition.name}, url=${connection.url})"
}