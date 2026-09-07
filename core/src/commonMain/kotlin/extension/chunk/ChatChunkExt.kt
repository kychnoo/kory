package io.kory.core.extension.chunk

import io.kory.core.chat.choice.ChatChunkChoice
import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.extension.choice.forEachChoice

suspend inline fun ChatChunk.forEachChoice(
    crossinline onChoice: suspend (choiceIndex: Int, choice: ChatChunkChoice) -> Unit,
) {
    choices.forEachChoice { choiceIndex, choice ->  onChoice(choiceIndex, choice) }
}