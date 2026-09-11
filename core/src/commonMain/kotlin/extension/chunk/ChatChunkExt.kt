package io.kory.core.extension.chunk

import io.kory.core.chat.choice.ChatChunkChoice
import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.extension.choice.forEachChoice

/**
 * Iterates over each choice in a [ChatChunk].
 *
 * Useful when dealing with multiple choices (e.g., from parallel tool calling)
 * to avoid flattening issues.
 *
 * @param onChoice Suspending function invoked for each choice with its index.
 * @sample examples.core.extensions.chunk.usingForEachChoiceInChatChunk
 */
suspend inline fun ChatChunk.forEachChoice(
    crossinline onChoice: suspend (choiceIndex: Int, choice: ChatChunkChoice) -> Unit,
) {
    choices.forEachChoice { choiceIndex, choice ->  onChoice(choiceIndex, choice) }
}