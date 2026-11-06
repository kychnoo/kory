package io.kory.core.mcp.serialization

import io.kory.core.mcp.definition.McpToolDefinition
import io.kory.core.mcp.extension.rpcId
import io.kory.core.mcp.initialization.McpImplementation
import io.kory.core.mcp.initialization.McpInitializeParams
import io.kory.core.mcp.initialization.McpInitializeResult
import io.kory.core.mcp.rpc.JsonRpcError
import io.kory.core.mcp.rpc.JsonRpcNotification
import io.kory.core.mcp.rpc.JsonRpcRequest
import io.kory.core.mcp.rpc.JsonRpcResponse
import io.kory.core.mcp.serialization.McpJson
import io.kory.core.mcp.tool.McpCallToolResult
import io.kory.core.mcp.tool.McpListToolsResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class McpJsonSerializationTest {

    private val json = McpJson

    @Test
    fun testJsonRpcRequestEncodesDefaults() {
        val request = JsonRpcRequest(id = 5, method = "tools/call", params = buildJsonObject { put("name", "add") })

        val element = json.encodeToString<JsonRpcRequest>(request).let { json.parseToJsonElement(it).jsonObject }

        assertEquals("2.0", element.getValue("jsonrpc").jsonPrimitive.content)
        assertEquals(5L, element.getValue("id").jsonPrimitive.long)
        assertEquals("tools/call", element.getValue("method").jsonPrimitive.content)
        assertEquals("add", element.getValue("params").jsonObject.getValue("name").jsonPrimitive.content)
    }

    @Test
    fun testJsonRpcRequestOmitsNullParams() {
        val element = json.encodeToString<JsonRpcRequest>(JsonRpcRequest(id = 2, method = "tools/list"))
            .let { json.parseToJsonElement(it).jsonObject }

        assertTrue(element.containsKey("jsonrpc"))
        assertTrue(element.containsKey("id"))
        assertTrue(element.containsKey("method"))
        assertFalse(element.containsKey("params"))
    }

    @Test
    fun testJsonRpcNotificationEncodesWithoutId() {
        val element = json.encodeToString<JsonRpcNotification>(
            JsonRpcNotification(method = "notifications/initialized")
        ).let { json.parseToJsonElement(it).jsonObject }

        assertEquals("2.0", element.getValue("jsonrpc").jsonPrimitive.content)
        assertEquals("notifications/initialized", element.getValue("method").jsonPrimitive.content)
        assertFalse(element.containsKey("id"))
        assertFalse(element.containsKey("params"))
    }

    @Test
    fun testJsonRpcResponseDecodesResult() {
        val response = json.decodeFromString<JsonRpcResponse>(
            """{"jsonrpc":"2.0","id":7,"result":{"x":1}}"""
        )

        assertEquals(7L, assertNotNull(response.id).jsonPrimitive.long)
        assertEquals(buildJsonObject { put("x", 1) }, response.result)
        assertNull(response.error)
    }

    @Test
    fun testJsonRpcResponseDecodesErrorWithData() {
        val response = json.decodeFromString<JsonRpcResponse>(
            """{"jsonrpc":"2.0","id":8,"error":{"code":-32601,"message":"no such method","data":{"hint":"use tools/list"}}}"""
        )

        val error = assertNotNull(response.error)
        assertEquals(-32601, error.code)
        assertEquals("no such method", error.message)
        assertEquals("use tools/list", error.data!!.jsonObject.getValue("hint").jsonPrimitive.content)
        assertNull(response.result)
    }

    @Test
    fun testJsonRpcResponseDecodesBareObject() {
        val response = json.decodeFromString<JsonRpcResponse>("""{"jsonrpc":"2.0","id":1}""")

        assertNull(response.result)
        assertNull(response.error)
    }

    @Test
    fun testJsonRpcErrorDecodesWithoutData() {
        val error = json.decodeFromString<JsonRpcError>("""{"code":42,"message":"boom"}""")

        assertEquals(42, error.code)
        assertEquals("boom", error.message)
        assertNull(error.data)
    }

    @Test
    fun testMcpInitializeParamsEncodesClientInfo() {
        val params = McpInitializeParams(
            protocolVersion = "2025-06-18",
            clientInfo = McpImplementation(name = "kory", version = "1.0.0"),
        )

        val element = json.encodeToString(McpInitializeParams.serializer(), params).let {
            json.parseToJsonElement(it).jsonObject
        }

        assertEquals("2025-06-18", element.getValue("protocolVersion").jsonPrimitive.content)
        assertEquals(JsonObject(emptyMap()), element.getValue("capabilities"))
        val clientInfo = element.getValue("clientInfo").jsonObject
        assertEquals("kory", clientInfo.getValue("name").jsonPrimitive.content)
        assertEquals("1.0.0", clientInfo.getValue("version").jsonPrimitive.content)
    }

    @Test
    fun testMcpInitializeResultDecodesMinimal() {
        val result = json.decodeFromString<McpInitializeResult>("""{"protocolVersion":"2025-11-25"}""")

        assertEquals("2025-11-25", result.protocolVersion)
        assertNull(result.capabilities)
        assertNull(result.serverInfo)
        assertNull(result.instructions)
    }

    @Test
    fun testMcpInitializeResultDecodesFull() {
        val result = json.decodeFromString<McpInitializeResult>(
            """{"protocolVersion":"2025-06-18","capabilities":{"tools":{}},""" +
                """"serverInfo":{"name":"fake","version":"1.2.0"},"instructions":"Be nice"}"""
        )

        assertEquals("2025-06-18", result.protocolVersion)
        assertNotNull(result.capabilities)
        assertEquals("fake", assertNotNull(result.serverInfo).name)
        assertEquals("1.2.0", result.serverInfo.version)
        assertEquals("Be nice", result.instructions)
    }

    @Test
    fun testMcpListToolsResultPagination() {
        val result = json.decodeFromString<McpListToolsResult>(
            """{"tools":[{"name":"a"},{"name":"b"}],"nextCursor":"page-2"}"""
        )

        assertEquals(listOf("a", "b"), result.tools.map { it.name })
        assertEquals("page-2", result.nextCursor)
    }

    @Test
    fun testMcpListToolsResultDefaults() {
        val result = json.decodeFromString<McpListToolsResult>("""{}""")

        assertTrue(result.tools.isEmpty())
        assertNull(result.nextCursor)
    }

    @Test
    fun testMcpToolDefinitionEncodesInputSchemaDefault() {
        val element = json.encodeToString(McpToolDefinition.serializer(), McpToolDefinition(name = "tool"))
            .let { json.parseToJsonElement(it).jsonObject }

        assertEquals(buildJsonObject { put("type", "object") }, element.getValue("inputSchema"))
        assertFalse(element.containsKey("outputSchema"))
        assertFalse(element.containsKey("description"))
        assertFalse(element.containsKey("title"))
    }

    @Test
    fun testMcpToolDefinitionDecodesWithUnknownKeys() {
        val definition = json.decodeFromString<McpToolDefinition>(
            """{"name":"tool","description":"d","inputSchema":{"type":"object","properties":{}},"unknownFutureField":true}"""
        )

        assertEquals("tool", definition.name)
        assertEquals("d", definition.description)
        assertNotNull(definition.inputSchema)
        assertEquals("object", definition.inputSchema.getValue("type").jsonPrimitive.content)
        assertNull(definition.outputSchema)
        assertNull(definition.title)
        assertNull(definition.annotations)
    }

    @Test
    fun testMcpCallToolResultDecodesStructuredContent() {
        val result = json.decodeFromString<McpCallToolResult>(
            """{"content":[{"type":"text","text":"hi"}],"isError":true,"structuredContent":{"answer":"42"}}"""
        )

        assertEquals(1, result.content.size)
        assertEquals("text", result.content[0].type)
        assertTrue(result.isError)
        assertEquals("42", assertNotNull(result.structuredContent).jsonObject.getValue("answer").jsonPrimitive.content)
    }

    @Test
    fun testMcpCallToolResultDefaults() {
        val result = json.decodeFromString<McpCallToolResult>("""{}""")

        assertTrue(result.content.isEmpty())
        assertFalse(result.isError)
        assertNull(result.structuredContent)
    }

    @Test
    fun testRpcIdExtractsLongId() {
        assertEquals(42L, json.parseToJsonElement("""{"id":42}""").jsonObject.rpcId())
    }

    @Test
    fun testRpcIdParsesStringNumber() {
        assertEquals(7L, json.parseToJsonElement("""{"id":"7"}""").jsonObject.rpcId())
    }

    @Test
    fun testRpcIdMissingOrNullForNonNumeric() {
        assertNull(json.parseToJsonElement("""{"method":"m"}""").jsonObject.rpcId())
        assertNull(json.parseToJsonElement("""{"id":"abc"}""").jsonObject.rpcId())
        // Preferred payload shapes (e.g. arrays) are not ids
        assertNull(json.parseToJsonElement("""{"id":[1]}""").jsonObject.rpcId())
    }
}
