package io.kory.openai.responses.serialization

import io.kory.openai.responses.message.content.OpenAIResponseInputContent
import io.kory.openai.responses.message.content.OpenAIResponseContent

object OpenAIResponsesContentSerializer : OpenAITextOrListSerializer<OpenAIResponseContent, OpenAIResponseInputContent>(
    itemSerializer = OpenAIResponseInputContent.serializer(),
    createText = { OpenAIResponseContent.Text(it) },
    createList = { OpenAIResponseContent.ResponseInputMessageContentList(it) },
    extractText = { (it as? OpenAIResponseContent.Text)?.value },
    extractList = { (it as? OpenAIResponseContent.ResponseInputMessageContentList)?.list }
)