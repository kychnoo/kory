package io.kory.core.extension.string

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content

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

fun String.asSystemMessage() = Message(Role.SYSTEM, Content.Text(this))
fun String.asUserMessage() = Message(Role.USER, Content.Text(this))
fun String.asAssistantMessage() = Message(Role.ASSISTANT, Content.Text(this))