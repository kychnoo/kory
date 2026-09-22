package io.kory.openai.responses.serialization

import io.kory.openai.responses.input.OpenAIInput
import io.kory.openai.responses.input.OpenAIInputItem

object OpenAIInputSerializer : OpenAITextOrListSerializer<OpenAIInput, OpenAIInputItem>(
    itemSerializer = OpenAIInputItem.serializer(),
    createText = { OpenAIInput.Text(it) },
    createList = { OpenAIInput.InputItemList(it) },
    extractText = { (it as? OpenAIInput.Text)?.value },
    extractList = { (it as? OpenAIInput.InputItemList)?.items }
)