package io.kory.core.extension.choice

import io.kory.core.chat.choice.ChatChunkChoice

/**
 * Iterates over each choice in a list of [ChatChunkChoice].
 *
 * @param onChoice Suspending function invoked for each choice with its index.
 * @sample examples.core.extensions.choice.usingForEachChoiceInChatChoices
 */
suspend inline fun List<ChatChunkChoice>.forEachChoice(
    crossinline onChoice: suspend (choiceIndex: Int, choice: ChatChunkChoice) -> Unit,
) {
    for (index in this.indices) {
        val choice = this[index]
        onChoice(choice.index, choice)
    }
}