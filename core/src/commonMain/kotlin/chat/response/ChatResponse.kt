package io.kory.core.chat.response

import io.kory.core.chat.choice.ChatChoice
import io.kory.core.chat.usage.TokensUsage
import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

/**
 * A non-streaming response from an LLM provider.
 *
 * Contains one or more [ChatChoice] objects representing different completion options.
 * In most cases there is a single choice, but some providers support returning
 * multiple candidates.
 *
 * @property choices The list of completion choices returned by the model.
 *
 * @see io.kory.core.chat.choice.ChatChoice
 * @see io.kory.core.message.content.Content
 *
 * @sample examples.core.chat.response.processChatResponse
 * @sample examples.core.chat.choice.processChatChoice
 */
@Serializable
data class ChatResponse(
    val choices: List<ChatChoice>,
    val usage: TokensUsage? = null
)
