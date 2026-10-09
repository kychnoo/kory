package io.kory.openai.responses.serialization

import io.kory.openai.responses.io.OpenAIResponseInput
import io.kory.openai.responses.io.items.OpenAIResponseInputItem

object OpenAIInputSerializer : OpenAITextOrListSerializer<OpenAIResponseInput, OpenAIResponseInputItem>(
    itemSerializer = OpenAIResponseInputItem.serializer(),
    createText = { OpenAIResponseInput.Text(it) },
    createList = { OpenAIResponseInput.InputItemList(it) },
    extractText = { (it as? OpenAIResponseInput.Text)?.value },
    extractList = { (it as? OpenAIResponseInput.InputItemList)?.items }
)