package io.kory.core.extension.choice

import io.kory.core.chat.choice.ChatChunkChoice

suspend inline fun List<ChatChunkChoice>.forEachChoice(
    crossinline onChoice: suspend (choiceIndex: Int, choice: ChatChunkChoice) -> Unit,
) {
    for (index in this.indices) {
        val choice = this[index]
        onChoice(choice.index, choice)
    }
}