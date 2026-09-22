package io.kory.openai.responses.dsl

import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.responses.message.content.OpenAIResponseContent
import io.kory.openai.responses.message.content.OpenAIResponseInputContent
import io.kory.openai.shared.param.OpenAIDetail

@OpenAIRequestDsl
class OpenAIResponseContentBuilder {
    private val contents = mutableListOf<OpenAIResponseInputContent>()

    fun text(text: String) {
        contents.add(OpenAIResponseInputContent.ResponseInputText(text))
    }

    fun image(imageUrl: String? = null, fileId: String? = null, detail: OpenAIDetail? = null) {
        contents.add(OpenAIResponseInputContent.ResponseInputImage(imageUrl = imageUrl, fileId = fileId, detail = detail))
    }

    fun imageUrl(url: String, detail: OpenAIDetail = OpenAIDetail.Responses.Image.Auto) {
        image(imageUrl = url, detail = detail)
    }

    fun imageId(id: String) {
        image(fileId = id)
    }

    fun file(
        fileData: String? = null,
        fileUrl: String? = null,
        fileName: String? = null,
        fileId: String? = null,
        detail: OpenAIDetail? = null
    ) {
        contents.add(OpenAIResponseInputContent.ResponseInputFile(
            fileData = fileData,
            fileUrl = fileUrl,
            fileName = fileName,
            fileId = fileId,
            detail = detail
        ))
    }

    fun fileUrl(url: String, detail: OpenAIDetail = OpenAIDetail.Responses.File.Auto) {
        file(fileUrl = url, detail = detail)
    }

    fun fileId(id: String, detail: OpenAIDetail = OpenAIDetail.Responses.File.Auto) {
        file(fileId = id, detail = detail)
    }

    fun fileData(data: String, fileName: String? = null) {
        file(fileData = data, fileName = fileName)
    }

    internal fun build(): OpenAIResponseContent {
        return if (contents.size == 1 && contents.first() is OpenAIResponseInputContent.ResponseInputText) {
            OpenAIResponseContent.Text((contents.first() as OpenAIResponseInputContent.ResponseInputText).text)
        } else {
            OpenAIResponseContent.ResponseInputMessageContentList(contents.toList())
        }
    }
}

fun openAIResponseContent(block: OpenAIResponseContentBuilder.() -> Unit): OpenAIResponseContent {
    return OpenAIResponseContentBuilder().apply(block).build()
}