package examples.openai.completions.stop

import io.kory.openai.stop.OpenAIStop

fun createOpenAIStop() {
    // Single sequence.
    val stopString = OpenAIStop.OpenAIString("###")
    // Multiple sequences.
    val stopList = OpenAIStop.OpenAIList(listOf("\n\n", "###"))
}