package io.kory.openai.responses.dsl

import io.kory.core.message.Role
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.responses.io.items.OpenAIResponsesEasyInputMessage
import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.items.OpenAIResponsesMcpApprovalResponse
import io.kory.openai.responses.io.items.OpenAIResponsesMessage
import io.kory.openai.responses.message.content.OpenAIResponseContent

/**
 * DSL builder for a list of [OpenAIResponseInputItem].
 *
 * @see openAIResponseInputItems
 */
@OpenAIRequestDsl
class OpenAIResponseInputItemBuilder {
    private var items = mutableListOf<OpenAIResponseInputItem>()

    /**
     * Adds a simple text input message.
     *
     * @param text The message text.
     * @param role The message role.
     */
    fun easyInputMessage(text: String, role: Role) {
        items.add(OpenAIResponsesEasyInputMessage(
            role = role,
            content = OpenAIResponseContent.Text(text)
        ))
    }

    /**
     * Adds a simple input message with pre-built content.
     *
     * @param role The message role.
     * @param content The message content.
     */
    fun easyInputMessage(role: Role, content: OpenAIResponseContent) {
        items.add(OpenAIResponsesEasyInputMessage(
            role = role,
            content = content
        ))
    }

    /**
     * Adds a simple multi-part input message using a DSL.
     *
     * @param role The message role.
     * @param block DSL for constructing the message content.
     */
    fun easyInputMessage(role: Role, block: OpenAIResponseContentBuilder.() -> Unit) {
        items.add(OpenAIResponsesEasyInputMessage(
                role = role,
                content = openAIResponseContent(block)
            )
        )
    }

    /**
     * Adds a structured input message with pre-built content.
     *
     * @param role The message role.
     * @param content The message content.
     */
    fun message(role: Role, content: OpenAIResponseContent) {
        items.add(OpenAIResponsesEasyInputMessage(
            role = role,
            content = content
        ))
    }

    /**
     * Adds a structured input message using a DSL.
     *
     * @param role The message role.
     * @param block DSL for constructing the message content.
     */
    fun message(role: Role, block: OpenAIResponseContentBuilder.() -> Unit) {
        items.add(OpenAIResponsesMessage(
            role = role,
            content = openAIResponseContent(block)
        ))
    }

    /**
     * Adds an MCP approval response input item.
     *
     * @param approvalRequestId The ID of the approval request being responded to.
     * @param approve Whether the request is approved.
     * @param reason Optional reason for the decision.
     * @param id Optional unique identifier for this item.
     */
    fun mcpApprovalResponse(
        approvalRequestId: String,
        approve: Boolean,
        reason: String? = null,
        id: String? = null
    ) {
        items.add(OpenAIResponsesMcpApprovalResponse(
                approvalRequestId = approvalRequestId,
                approve = approve,
                reason = reason,
                id = id
            )
        )
    }

    internal fun build(): List<OpenAIResponseInputItem> {
        return items.toList()
    }
}

/**
 * Builds a list of [OpenAIResponseInputItem] using a DSL-style builder.
 *
 * @param block Lambda for configuring the input items.
 * @return A list of [OpenAIResponseInputItem] instances.
 *
 * @see OpenAIResponseInputItemBuilder
 */
fun openAIResponseInputItems(block: OpenAIResponseInputItemBuilder.() -> Unit): List<OpenAIResponseInputItem> {
    return OpenAIResponseInputItemBuilder().apply(block).build()
}

