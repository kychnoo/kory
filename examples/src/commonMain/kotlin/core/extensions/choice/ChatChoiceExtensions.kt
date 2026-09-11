package examples.core.extensions.choice

import io.kory.core.chat.choice.ChatChunkChoice
import io.kory.core.extension.choice.forEachChoice
import io.kory.core.message.content.Content

suspend fun usingForEachChoiceInChatChoices(choices: List<ChatChunkChoice>) {
    // forEachChoice iterates over each Choice, eliminating the effects of FlatMap
    // (when more Choices were being generated due to parallel ToolCalling)
    choices.forEachChoice { choiceIndex, choice ->
        val output = when (val content = choice.content) {
            is Content.Reasoning -> "reasoning: ${content.value}"
            is Content.Text -> "output: ${content.text}"
            is Content.ToolCallDelta -> "tool call: ${content.name}"
        }

        println("Choice: $choiceIndex: $output")
    }
}