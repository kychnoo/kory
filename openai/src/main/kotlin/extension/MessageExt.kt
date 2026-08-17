package io.kory.openai.extension

import io.kory.core.message.Message
import io.kory.openai.message.OpenAIMessage

fun Message.toOpenAIMessage(): OpenAIMessage = OpenAIMessage(
    role = this.role,
    content = content.toOpenAIContent()
)

fun List<Message>.toOpenAIMessageList(): List<OpenAIMessage> = map { it.toOpenAIMessage() }