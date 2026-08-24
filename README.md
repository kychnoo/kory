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

fun main() {
    // Read API key from environment
    val apiKey = System.getenv("OPEN_AI_API_KEY") ?: error("Error: No api key provided")

    // Create an OpenAI-compatible client.
    // Works with any provider that exposes an OpenAI-compatible API.
    val client = OpenAIClient(
        apiKey = apiKey
    )

    runBlocking {
        // Send a chat request using the DSL.
        // `chat` builds the message list, the client handles serialization and HTTP.
        val response: ChatResponse? = try {
            client.chat {
                chat(model = "model-name") {
                    user("Ping!")
                }
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }

        // Process the response.
        response?.let { resp ->
            // A response contains one or more choices (usually one).
            for (choice in resp.choices) {
                // Each choice contains a list of content items:
                // text, reasoning, tool calls, or multi-part content.
                for (content in choice.contents) {
                    when (content) {
                        is Content.Parts -> println("Parts: ${content.parts.joinToString(separator = "\n")}")
                        is Content.Reasoning -> println("Reasoning: ${content.value}")
                        is Content.Text -> println("Text: ${content.text}")
                        is Content.ToolCall -> println("Tool call: ${content.name} with ${content.argumentsJson}")
                    }
                }
            }
        }
    }
}
```

### Streaming

```kotlin
client.chatStream {
    chat(model = "model-name") {
        user("Tell me a story")
    }
}.collect { chunk ->
    for (choice in chunk.choices) {
        when (val content = choice.content) {
            is Content.Text -> print(content.text)
            is Content.Reasoning -> print("[thinking] ${content.value}")
            else -> {}
        }
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

KDoc is generated for all public APIs. Run:

```bash
./gradlew dokkaHtml
```

## Roadmap

- [ ] File upload support (images, documents)
- [ ] MCP (Model Context Protocol) integration
- [ ] Skills system
- [ ] More API providers (Anthropic, Google, etc.)

## Building

```bash
./gradlew build          # Build all modules
./gradlew :core:test     # Run core tests
./gradlew :openai:test   # Run OpenAI module tests
```

## License
#### This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
