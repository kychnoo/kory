package io.kory.openai.samples.completions.stop

import io.kory.openai.shared.param.OpenAIStop

fun createOpenAIStop() {
    // Single sequence.
    val stopString = OpenAIStop.OpenAIString("###")
    // Multiple sequences.
    val stopList = OpenAIStop.OpenAIList(listOf("\n\n", "###"))
}