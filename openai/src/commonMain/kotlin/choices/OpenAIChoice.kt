package io.kory.openai.choices

import io.kory.core.chat.choice.ChatChoice
import io.kory.core.extension.string.parseThinkContent
import io.kory.core.message.content.Content
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.message.OpenAIMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A choice in an OpenAI chat completion response.
 *
 * @property index The index of this choice.
 * @property message The message returned by the model.
 * @property finishReason Why the model stopped: `"stop"`, `"length"`, `"tool_calls"`, etc.
 */
@Serializable
data class OpenAIChoice(
    val index: Int,
    val message: OpenAIMessage,
    @SerialName("finish_reason")
    val finishReason: String? = null,
) : Mapper<ChatChoice> {
    override fun map(): ChatChoice {
        val responseContents = mutableListOf<Content.Response>()

        message.content?.toResponseContent()?.let { content ->
            if (content is Content.Text) {
                val contentText = content.text
                responseContents.addAll(contentText.parseThinkContent())
            } else {
                responseContents.add(content)
            }
        }

        message.effectiveReasoning?.let { reasoning ->
            responseContents.add(Content.Reasoning(reasoning))
        }

        message.toolCalls?.forEach { toolCall ->
            responseContents.add(
                Content.ToolCall(
                    id = toolCall.id.orEmpty(),
                    name = toolCall.function.name.orEmpty(),
                    argumentsJson = toolCall.function.arguments?.takeIf { it.isNotBlank() } ?: "{}"
                )
            )
        }

        return ChatChoice(
            index = index,
            contents = responseContents,
            finishReason = finishReason
        )
    }

    /**
     * Converts this to a core [ChatChoice].
     *
     * @return A [ChatChoice] with mapped content.
     */
    fun toChatChoice(): ChatChoice = map()
}
