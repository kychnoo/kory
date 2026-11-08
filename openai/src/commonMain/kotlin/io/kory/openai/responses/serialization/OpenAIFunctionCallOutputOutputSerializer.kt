package io.kory.openai.responses.serialization

import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.items.function.OpenAIFunctionCallOutputOutput

object OpenAIFunctionCallOutputOutputSerializer : OpenAITextOrListSerializer<OpenAIFunctionCallOutputOutput, OpenAIResponseInputItem>(
    itemSerializer = OpenAIResponseInputItem.serializer(),
    createText = { OpenAIFunctionCallOutputOutput.StringOutput(it) },
    createList = { OpenAIFunctionCallOutputOutput.OutputContentList(it) },
    extractText = { (it as? OpenAIFunctionCallOutputOutput.StringOutput)?.value },
    extractList = { (it as? OpenAIFunctionCallOutputOutput.OutputContentList)?.items }
)