package examples.openai.client

import examples.core.tools.TestWeatherTool
import io.kory.core.chat.Chat
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.koryChat
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient

suspend fun sendToChatWithChatRequest(client: OpenAIClient, modelName: String) {
    // Send a ChatRequest to OpenAI.

    // Create a chat using koryChat DSL function.
    val chat: Chat = koryChat(model = modelName) {
        user("Ping!")
    }

    val request: ChatRequest = chat.asChatRequest(
        temperature = 1.4
    )

    // Send request to OpenAI API.
    val response: ChatResponse? = try {
        client.chat(request)
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }

    response?.let { response ->
        // Iterate all choices.
        for (choice in response.choices) {
            // Iterate through all the content from choice
            for (content in choice.contents) {
                // Print information about content.
                when (content) { // Content is Content.Response.
                    is Content.Parts -> {
                        println("Parts: ${content.parts.joinToString(separator = "\n")}")
                    }
                    is Content.Reasoning -> println("Reasoning: ${content.value}")
                    is Content.Text -> println("Text: ${content.text}")
                    is Content.ToolCall -> println("Tool call: ${content.name} with ${content.argumentsJson}")
                }
            }
        }
    }
}

suspend fun sendToChatWithChatDsl(client: OpenAIClient, modelName: String) {
    // Send a request to OpenAI using chat() function with chat DSL.

    val response: ChatResponse? = try {
        client.chat(modelName) {
            // Body for messages
            user("Ping!")
        }

        // This function does not support parameters for ChatRequest.
        // If you need to add `tools`, `temperature`, or other specific parameters,
        // take a look at the `chat()` function with `ChatRequestBuilder`.
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }

    response?.let { response ->
        // Iterate all choices.
        for (choice in response.choices) {
            // Iterate through all the content from choice
            for (content in choice.contents) {
                // Print information about content.
                when (content) { // Content is Content.Response.
                    is Content.Parts -> {
                        println("Parts: ${content.parts.joinToString(separator = "\n")}")
                    }
                    is Content.Reasoning -> println("Reasoning: ${content.value}")
                    is Content.Text -> println("Text: ${content.text}")
                    is Content.ToolCall -> println("Tool call: ${content.name} with ${content.argumentsJson}")
                }
            }
        }
    }
}

suspend fun sendToChatWithChatRequestDsl(client: OpenAIClient, modelName: String) {
    // Send a request to OpenAI using chat() function with chat request DSL.

    val response: ChatResponse? = try {
        client.chat {
            temperature = 1.4 // Set temperature.

            chat(model = modelName) { // Chat function with dsl for messages.
                user("Ping!")
            }
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }

    response?.let { response ->
        // Iterate all choices.
        for (choice in response.choices) {
            // Iterate through all the content from choice
            for (content in choice.contents) {
                // Print information about content.
                when (content) { // Content is Content.Response.
                    is Content.Parts -> {
                        println("Parts: ${content.parts.joinToString(separator = "\n")}")
                    }
                    is Content.Reasoning -> println("Reasoning: ${content.value}")
                    is Content.Text -> println("Text: ${content.text}")
                    is Content.ToolCall -> println("Tool call: ${content.name} with ${content.argumentsJson}")
                }
            }
        }
    }
}

suspend fun sendToChatStreamWithRequest(client: OpenAIClient, modelName: String) {
    // Send a stream ChatRequest to OpenAI.

    // Create a chat using koryChat DSL function.
    val chat: Chat = koryChat(model = modelName) {
        user("Ping!")
    }

    val request: ChatRequest = chat.asChatRequest(
        temperature = 1.4
    )

    // ChatStream uses a flow to stream chunks
    client.chatStream(request).collect { chunk ->
        // Iterate all choices in chunk.
        for (choice in chunk.choices) {
            // Print information about content.
            when (val content = choice.content) { // Content is Content.StreamResponse.
                is Content.Reasoning -> println("Reasoning: ${content.value}")
                is Content.Text -> println("Text: ${content.text}")
                is Content.ToolCallDelta -> println("Tool call: ${content.name} with ${content.argumentsChunk ?: "no arguments"}")
            }
        }
    }
}

suspend fun sendToChatStreamWithDsl(client: OpenAIClient, modelName: String) {
    // Send a stream request to OpenAI using chat() function with chat DSL.

    client.chatStream(model = modelName) {
        user("Ping!")
    }.collect { chunk ->
        // Iterate all choices in chunk.
        for (choice in chunk.choices) {
            // Print information about content.
            when (val content = choice.content) { // Content is Content.StreamResponse.
                is Content.Reasoning -> println("Reasoning: ${content.value}")
                is Content.Text -> println("Text: ${content.text}")
                is Content.ToolCallDelta -> println("Tool call: ${content.name} with ${content.argumentsChunk ?: "no arguments"}")
            }
        }
    }
}

suspend fun sendToChatStreamWithRequestBuilder(client: OpenAIClient, modelName: String) {
    // Send a request to OpenAI using chat() function with chat request DSL.

    client.chatStream {
        temperature = 1.4 // Set temperature.

        chat(model = modelName) { // Chat function with dsl for messages.
            user("Ping!")
        }
    }.collect { chunk ->
        // Iterate all choices in chunk.
        for (choice in chunk.choices) {
            // Print information about content.
            when (val content = choice.content) { // Content is Content.StreamResponse.
                is Content.Reasoning -> println("Reasoning: ${content.value}")
                is Content.Text -> println("Text: ${content.text}")
                is Content.ToolCallDelta -> println("Tool call: ${content.name} with ${content.argumentsChunk ?: "no arguments"}")
            }
        }
    }
}

// Requests with tools.
suspend fun sendToChatWithToolsRequest(client: OpenAIClient, modelName: String) {
    // Send a request to OpenAI with tools using ChatRequest.

    // Init TestWeatherTool.
    val getWeatherTool = TestWeatherTool()

    val chat: Chat = koryChat(model = modelName) {
        user("What's the weather like in London?")
    }

    val request: ChatRequest = chat.asChatRequest(
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW), // Low reasoning so that the model can think.
        temperature = 1.4,
        // Add getWeatherTool.
        tools = {
            tool(getWeatherTool)
        }
    )

    // Send request to OpenAI.
    val response = try {
        // The tools will execute automatically.
        client.chatWithTools(
            request = request,
            onToolCall = { name, argsJson ->
                // This callback will be triggered when the AI calls the tool.
                println("AI called tool: $name with args: $argsJson")
            }
        )
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }

    response?.let { response ->
        // Iterate all choices.
        for (choice in response.choices) {
            // Iterate through all the content from choice
            for (content in choice.contents) {
                // Print information about content.
                when (content) { // Content is Content.Response.
                    is Content.Parts -> {
                        println("Parts: ${content.parts.joinToString(separator = "\n")}")
                    }
                    is Content.Reasoning -> println("Reasoning: ${content.value}")
                    is Content.Text -> println("Text: ${content.text}")
                    is Content.ToolCall -> println("Tool call: ${content.name} with ${content.argumentsJson}")
                }
            }
        }
    }
}

suspend fun sendToChatWithToolsRequestBuilder(client: OpenAIClient, modelName: String) {
    // Send a request to OpenAI with tools using chat() function with chat request DSL.

    // Init TestWeatherTool.
    val getWeatherTool = TestWeatherTool()

    val response: ChatResponse? = try {
        // The tools will execute automatically.
        client.chatWithTools(
            onToolCall = { name, argsJson ->
                // This callback will be triggered when the AI calls the tool.
                println("AI called tool: $name with args: $argsJson")
            }
        ) {
            reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW) // Low reasoning so that the model can think.
            temperature = 1.4 // Set temperature.

            chat(model = modelName) { // Chat function with dsl for messages.
                user("What's the weather like in London?")
            }

            // Register getWeatherTool.
            registerTools(getWeatherTool)
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }

    response?.let { response ->
        // Iterate all choices.
        for (choice in response.choices) {
            // Iterate through all the content from choice
            for (content in choice.contents) {
                // Print information about content.
                when (content) { // Content is Content.Response.
                    is Content.Parts -> {
                        println("Parts: ${content.parts.joinToString(separator = "\n")}")
                    }
                    is Content.Reasoning -> println("Reasoning: ${content.value}")
                    is Content.Text -> println("Text: ${content.text}")
                    is Content.ToolCall -> println("Tool call: ${content.name} with ${content.argumentsJson}")
                }
            }
        }
    }
}

suspend fun sendToChatStreamWithToolsRequest(client: OpenAIClient, modelName: String) {
    // Send a stream request to OpenAI with tools using chat() function with chat request.

    // Init TestWeatherTool.
    val getWeatherTool = TestWeatherTool()

    // Create a chat using koryChat DSL function.
    val chat: Chat = koryChat(model = modelName) {
        user("What's the weather like in London?")
    }

    val request: ChatRequest = chat.asChatRequest(
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW), // Low reasoning so that the model can think.
        temperature = 1.4,
        tools = {
            tool(getWeatherTool)
        }
    )

    // ChatStream uses a flow to stream chunks
    // The tools will execute automatically.
    client.chatStreamWithTools(
        request = request,
        onToolCall = { name, argsJson ->
            // This callback will be triggered when the AI calls the tool.
            println("AI called tool: $name with args: $argsJson")
        },
        onFullToolCollected = { name, argsJson ->
            // This callback will be triggered when the library has fully assembled the message regarding the tool call.
            println("Full tool call: $name with args: $argsJson")
        }
    ).collect { chunk ->
        // Iterate all choices in chunk.
        for (choice in chunk.choices) {
            // Print information about content.
            when (val content = choice.content) { // Content is Content.StreamResponse.
                is Content.Reasoning -> println("Reasoning: ${content.value}")
                is Content.Text -> println("Text: ${content.text}")
                is Content.ToolCallDelta -> println("Tool call: ${content.name} with ${content.argumentsChunk ?: "no arguments"}")
            }
        }
    }
}

suspend fun sendToChatStreamWithToolsRequestBuilder(client: OpenAIClient, modelName: String) {
    // Send a stream request to OpenAI with tools using chat() function with chat request DSL.

    // Init TestWeatherTool.
    val getWeatherTool = TestWeatherTool()

    // The tools will execute automatically.
    client.chatStreamWithTools(
        onToolCall = { name, argsJson ->
            // This callback will be triggered when the AI calls the tool.
            println("AI called tool: $name with args: $argsJson")
        },
        onFullToolCollected = { name, argsJson ->
            // This callback will be triggered when the library has fully assembled the message regarding the tool call.
            println("Full tool call: $name with args: $argsJson")
        }
    ) {
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW) // Low reasoning so that the model can think.
        temperature = 1.4 // Set temperature.

        // Register getWeatherTool.
        registerTools(getWeatherTool)

        chat(model = modelName) { // Chat function with dsl for messages.
            user("Ping!")
        }
    }.collect { chunk ->
        // Iterate all choices in chunk.
        for (choice in chunk.choices) {
            // Print information about content.
            when (val content = choice.content) { // Content is Content.StreamResponse.
                is Content.Reasoning -> println("Reasoning: ${content.value}")
                is Content.Text -> println("Text: ${content.text}")
                is Content.ToolCallDelta -> println("Tool call: ${content.name} with ${content.argumentsChunk ?: "no arguments"}")
            }
        }
    }
}