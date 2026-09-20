# Kory

Kotlin-first multi-module library for working with AI providers. Provides idiomatic DSL and clients for LLM interactions.

## Features

- **Kotlin-First DSL** — build chats, requests, and tools with type-safe builders.
- **Multi-provider support** — works with any OpenAI-compatible API (OpenAI, Ollama, Groq, etc.).
- **Tool execution** — automatic tool call loop: the client parses arguments, executes tools, and continues the conversation until a text response is produced.
- **Streaming** — receive model output token-by-token via `Flow<ChatChunk>`.
- **Reasoning** — configure thinking/reasoning depth for models that support it.
- **Serialization** — all models are `@Serializable` with `kotlinx.serialization`.

## Modules

| Module | Description |
|--------|-------------|
| `core` | Provider-agnostic abstractions: `Chat`, `Message`, `Content`, `KoryTool`, DSL builders, extensions. |
| `openai` | OpenAI-compatible client (`OpenAIClient`), request/response models, converters from `core` types. |
| `kory-ktor` | HTTP transport layer built on Ktor: `KoryHttpClient`, auth strategies, error handling. |
| `kory-ktor-cio` | CIO engine factory for `kory-ktor` (auto-discovered via ServiceLoader). |

## Quick Start

### Basic Usage

```kotlin
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import io.kory.core.chat.response.ChatResponse
import kotlinx.coroutines.runBlocking
import io.kory.core.chat.client.ApiKey

fun main() = runBlocking {
    val apiKey = ApiKey.fromEnv("OPENAI_API_KEY") { e ->
        // This callback is triggered if the key was not found in the system environment.
        println("Received exception: ${e.message}, using fallback key...")
        "Put your fallback api key here"
    }

    // Create an OpenAI-compatible client.
    // Works with any provider that exposes an OpenAI-compatible API.
    val client = OpenAIClient(
        apiKey = apiKey
    )

    // Send a catchable chat request using the DSL.
    // `chat` builds the message list, the client handles serialization and HTTP.
    client.chatCatching {
        chat(model = "model-name") {
            user("Ping!")
        }
    }.onSuccess { response ->
        // Process the response.
        response.choices.forEach { choice ->
            choice.contents.forEach { content ->
                // Each choice contains a list of content items:
                // text, reasoning, tool calls, or multi-part content.
                val exitContentText = when (content) {
                    is Content.Parts -> "Parts: ${content.parts.joinToString(separator = "\n")}"
                    is Content.Reasoning -> "Reasoning: ${content.value}"
                    is Content.Text -> "Text: ${content.text}"
                    is Content.ToolCall -> "Tool call: ${content.name} with ${content.argumentsJson}"
                }

                println("Exit content is: $exitContentText")
            }
        }
    }.onFailure { error ->
        // onFailure will be called if the function throws an exception.
        println(error.message)
    }
}
```

### Streaming

```kotlin
client.chatStream {
    chat(model = "model-name") {
        user("Tell me a story")
    }
}.collectHandler {
    // Custom collect handler.
    onChunk { chunk ->
        // Called on every chunk.
        for (choice in chunk.choices) {
            when (val content = choice.content) {
                is Content.Text -> println(content.text)
                is Content.Reasoning -> println("[thinking] ${content.value}")
                else -> {}
            }
        }
    }
    onError { error ->
        // Called if an error occurs during streaming.
        println("${error::class.simpleName}: ${error.message}")
    }
    onCompleted {
        // Called when streaming ends.
        println("Stream finished!")
    }
}
```

### Tools

```kotlin
// 1. Define a tool
class WeatherTool : KoryTool<WeatherTool.Args, String>(
    name = "get_weather",
    description = "Get weather in a city",
) {
    @Serializable
    data class Args(
        @ToolParam(description = "City name")
        val city: String
    )

    override val argsSerializer = serializer<Args>()

    override suspend fun execute(args: Args): String =
        "Sunny, 22°C in ${args.city}"
}

// 2. Use with automatic execution
val response = client.chatWithTools {
    registerTools { tool(WeatherTool()) }
    chat("model-name") {
        user("What's the weather in Moscow?")
    }
}
```

### Reasoning

```kotlin
val response = client.chat {
    reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.HIGH)
    chat("model-name") {
        user("Explain quantum computing")
    }
}
```

## Documentation

You can view the documentation [here](https://kychnoo.github.io/kory/)

Or generate it using

```bash
./gradlew dokkaGenerateHtml
```

## Roadmap

- [ ] File upload support (images, documents)
- [ ] MCP (Model Context Protocol) integration
- [ ] Skills system
- [ ] More API providers (Anthropic, Google, etc.)

## Building

```bash
./gradlew build          # Build all modules
./gradlew :core:allTest     # Run core tests
./gradlew :openai:allTest   # Run OpenAI module tests
```

## License
#### This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
