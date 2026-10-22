package io.kory.openai.responses.data

import io.kory.core.message.Role
import io.kory.core.tool.ToolExecutionConfig
import io.kory.core.utils.mapper.DtoMapper
import io.kory.openai.responses.dto.OpenAIResponsesRequestDto
import io.kory.openai.responses.dto.OpenAIResponsesResponse
import io.kory.openai.responses.io.OpenAIResponseInput
import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.items.OpenAIResponseOutputItem
import io.kory.openai.responses.io.items.OpenAIResponsesEasyInputMessage
import io.kory.openai.responses.io.tool.OpenAIResponsesTool
import io.kory.openai.responses.message.content.OpenAIResponseContent
import io.kory.openai.shared.param.OpenAIMetadata

data class OpenAIResponsesRequest(
    val model: String,
    val input: OpenAIResponseInput,
    val instructions: String? = null,
    val maxOutputTokens: Int? = null,
    val maxToolCalls: Int? = null,
    val metadata: OpenAIMetadata? = null,
    val background: Boolean = false,
    val tools: List<OpenAIResponsesTool<*>> = emptyList(),
    val toolExecutionConfig: ToolExecutionConfig = ToolExecutionConfig(),
) : DtoMapper<OpenAIResponsesRequestDto> {
    override fun toDto(): OpenAIResponsesRequestDto {
        return OpenAIResponsesRequestDto(
            model = model,
            input = input,
            instructions = instructions,
            tools = tools.map { it.toDto() },
        )
    }

    fun addInputItems(items: List<OpenAIResponseInputItem>): OpenAIResponsesRequest = this.copy(
        input = when (val content = input) {
            is OpenAIResponseInput.InputItemList -> OpenAIResponseInput.InputItemList(content.items + items)
            is OpenAIResponseInput.Text -> OpenAIResponseInput.InputItemList(
                listOf(OpenAIResponsesEasyInputMessage(
                    role = Role.User,
                    content = OpenAIResponseContent.Text(content.value)
                )) + items)
        }
    )

    fun addOutputItems(items: List<OpenAIResponseOutputItem>): OpenAIResponsesRequest = addInputItems(items.filterIsInstance<OpenAIResponseInputItem>())

    fun addItems(response: OpenAIResponsesResponse) = addOutputItems(response.output)
}