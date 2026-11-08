package io.kory.core.mcp.http

/**
 * Raw HTTP response from the MCP endpoint.
 *
 * @property status HTTP status code.
 * @property headers Response headers with lowercase keys; repeated headers are
 *   joined with `,` (irrelevant for `mcp-session-id` and friends).
 * @property contentType Lowercased content type without parameters,
 *   e.g. `application/json`; `null` if absent.
 * @property body Parsed body.
 */
class McpHttpResponse(
    val status: Int,
    val headers: Map<String, String>,
    val contentType: String?,
    val body: McpResponseBody,
) {
    /** Value of the `Mcp-Session-Id` response header, if present. */
    val sessionId: String?
        get() = headers["mcp-session-id"]
}