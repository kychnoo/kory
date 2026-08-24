package io.kory.core.extension.string

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content

/**
 * Parses ``<think>...</think>`` blocks from model output into structured content.
 *
 * Returns a list containing:
 * - [Content.Reasoning] for each non-blank think block.
 * - [Content.Text] for the remaining output text (after removing think blocks).
 *
 * @return A list of [Content.Response] items (reasoning + text).
 *
 * @sample examples.core.extensions.useParseThinkContent
 */
fun String.parseThinkContent(): List<Content.Response> {
    val result = mutableListOf<Content.Response>()
    val thinkRegex = Regex("""<think>(.*?)</think>""", RegexOption.DOT_MATCHES_ALL)

    thinkRegex.findAll(this).forEach { matchResult ->
        val reasoningContent = matchResult.groupValues[1].trim()
        if (reasoningContent.isNotBlank()) {
            result.add(Content.Reasoning(reasoningContent))
        }
    }

    val outputText = this.replace(thinkRegex, "").trim()
    if (outputText.isNotBlank()) {
        result.add(Content.Text(outputText))
    }

    return result
}

/**
 * Wraps this string as a system [Message].
 *
 * @return A [Message] with [Role.SYSTEM] and [Content.Text] content.
 *
 * @sample examples.core.extensions.useAsRoleMessage
 */
fun String.asSystemMessage() = Message(Role.SYSTEM, Content.Text(this))

/**
 * Wraps this string as a user [Message].
 *
 * @return A [Message] with [Role.USER] and [Content.Text] content.
 *
 * @sample examples.core.extensions.useAsRoleMessage
 */
fun String.asUserMessage() = Message(Role.USER, Content.Text(this))

/**
 * Wraps this string as an assistant [Message].
 *
 * @return A [Message] with [Role.ASSISTANT] and [Content.Text] content.
 *
 * @sample examples.core.extensions.useAsRoleMessage
 */
fun String.asAssistantMessage() = Message(Role.ASSISTANT, Content.Text(this))