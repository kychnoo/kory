package io.kory.openai.dsl.message

import io.kory.openai.dsl.message.content.OpenAIChatCompletionContentBuilder
import io.kory.openai.dsl.message.content.openAIChatCompletionContent
import io.kory.openai.dsl.request.OpenAIRequestDsl
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.content.audio.OpenAIAssistantAudioParam
import io.kory.openai.message.param.OpenAIMessageParam
import io.kory.openai.tool.OpenAIFunctionCall
import io.kory.openai.tool.OpenAIToolCall

@OpenAIRequestDsl
class OpenAIMessageParamsBuilder internal constructor() {
    private val messageParams = mutableListOf<OpenAIMessageParam>()

    fun user(content: OpenAIChatCompletionContent, name: String? = null) {
        messageParams.add(OpenAIMessageParam.User(content, name))
    }

    fun user(text: String, name: String? = null) {
        user(OpenAIChatCompletionContent.Text(text), name)
    }

    fun user(name: String? = null, content: OpenAIChatCompletionContentBuilder.() -> Unit) {
        user(openAIChatCompletionContent(content), name)
    }

    fun system(content: OpenAIChatCompletionContent, name: String? = null) {
        messageParams.add(OpenAIMessageParam.System(content, name))
    }

    fun system(text: String, name: String? = null) {
        system(OpenAIChatCompletionContent.Text(text), name)
    }

    fun system(name: String? = null, content: OpenAIChatCompletionContentBuilder.() -> Unit) {
        system(openAIChatCompletionContent(content), name)
    }

    fun developer(content: OpenAIChatCompletionContent, name: String? = null) {
        messageParams.add(OpenAIMessageParam.Developer(content, name))
    }

    fun developer(text: String, name: String? = null) {
        developer(OpenAIChatCompletionContent.Text(text), name)
    }

    fun developer(name: String? = null, content: OpenAIChatCompletionContentBuilder.() -> Unit) {
        developer(openAIChatCompletionContent(content), name)
    }

    fun assistant(
        content: OpenAIChatCompletionContent? = null,
        name: String? = null,
        refusal: String? = null,
        audio: OpenAIAssistantAudioParam? = null,
        functionCall: OpenAIFunctionCall? = null,
        toolCalls: List<OpenAIToolCall>? = null
    ) {
        messageParams.add(OpenAIMessageParam.Assistant(
            content, name, refusal, audio, functionCall, toolCalls
        ))
    }

    fun assistant(
        text: String,
        name: String? = null,
        refusal: String? = null,
        audio: OpenAIAssistantAudioParam? = null,
        functionCall: OpenAIFunctionCall? = null,
        toolCalls: List<OpenAIToolCall>? = null
    ) {
        assistant(
            OpenAIChatCompletionContent.Text(text),
            name,
            refusal,
            audio,
            functionCall,
            toolCalls
        )
    }

    fun assistant(
        name: String? = null,
        refusal: String? = null,
        audio: OpenAIAssistantAudioParam? = null,
        functionCall: OpenAIFunctionCall? = null,
        toolCalls: List<OpenAIToolCall>? = null,
        contentBuilder: (OpenAIChatCompletionContentBuilder.() -> Unit)? = null
    ) {
        val content = if (contentBuilder != null) openAIChatCompletionContent(contentBuilder) else null
        assistant(
            content,
            name,
            refusal,
            audio,
            functionCall,
            toolCalls
        )
    }

    fun tool(
        content: OpenAIChatCompletionContent,
        toolCallId: String,
        name: String? = null,
    ) {
        messageParams.add(OpenAIMessageParam.Tool(content, toolCallId, name))
    }

    fun tool(
        toolCallId: String,
        name: String? = null,
        content: OpenAIChatCompletionContentBuilder.() -> Unit,
    ) {
        tool(openAIChatCompletionContent(content), toolCallId, name)
    }

    internal fun build() : List<OpenAIMessageParam> {
        return messageParams.toList()
    }
}

fun openAIMessageParams(block: OpenAIMessageParamsBuilder.() -> Unit): List<OpenAIMessageParam> {
    return OpenAIMessageParamsBuilder().apply(block).build()
}