<div align="center">

# Kory

Kotlin-first multi-module library for working with AI providers. Provides idiomatic DSL and clients for LLM interactions.

<p>
    <a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/badge/License-Apache 2.0-blue?logo=apache&logoColor=white" alt="License"></a>
    <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-2.4.10-blueviolet?logo=kotlin&logoColor=white" alt="Kotlin"></a>
    <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Ktor-3.4.1-purple?logo=ktor&logoColor=white" alt="Kotlin"></a>
    <a href="https://gradle.org/"><img src="https://img.shields.io/badge/Gradle-9.7.1-blue?logo=gradle&logoColor=white" alt="Gradle"></a>
    <a href="https://github.com/kychnoo/kory"><img src="https://img.shields.io/github/actions/workflow/status/kychnoo/kory/ci.yml" alt="Build"></a>
    <a href="https://central.sonatype.com/artifact/io.github.kychnoo/kory-core"><img src="https://img.shields.io/maven-central/v/io.github.kychnoo/kory-core" alt="Maven Central"></a>
</p>

</div>

## Features

- **Kotlin-First DSL** — build chats, requests, and tools with type-safe builders.
- **Multi-provider support** — works with any OpenAI-compatible API (OpenAI, Ollama, Groq, etc.).
- **Tool execution** — automatic tool call loop: the client parses arguments, executes tools, and continues the conversation until a text response is produced.
- **Streaming** — receive model output token-by-token via `Flow<ChatChunk>`.
- **Reasoning** — configure thinking/reasoning depth for models that support it.
- **Serialization** — all models are `@Serializable` with `kotlinx.serialization`.

## Modules

| Module             | Description                                                                                         |
|--------------------|-----------------------------------------------------------------------------------------------------|
| `core`             | Provider-agnostic abstractions: `Chat`, `Message`, `Content`, `KoryTool`, DSL builders, extensions. |
| `openai`           | OpenAI-compatible client (`OpenAIClient`), request/response models, converters from `core` types.   |
| `kory-ktor`        | HTTP transport layer built on Ktor: `KoryHttpClient`, auth strategies, error handling.              |
| `kory-ktor-cio`    | CIO engine factory for `kory-ktor` (auto-discovered via ServiceLoader).                             |
| `kory-ktor-okhttp` | OkHttp engine factory for `kory-ktor` (auto-discovered via ServiceLoader).                          |

### Quick Start

Add dependencies in:

Gradle kotlin:
```gradle
// build.gradle.kts
val koryVersion = "0.0.1"

dependencies {
    implementation("io.github.kychnoo:kory-core:$koryVersion")
    implementation("io.github.kychnoo:kory-openai:$koryVersion")
    implementation("io.github.kychnoo:kory-ktor:$koryVersion")

    // Add any network engine you want (for the JVM, not required for native code).
    implementation("io.github.kychnoo:kory-ktor-cio:$koryVersion")
    implementation("io.github.kychnoo:kory-ktor-okhttp:$koryVersion")
}
```

Gradle groovy:
```gradle
// build.gradle
def koryVersion = "0.0.1"

dependencies {
    implementation "io.github.kychnoo:kory-core:${koryVersion}"
    implementation "io.github.kychnoo:kory-openai:${koryVersion}"
    implementation "io.github.kychnoo:kory-ktor:${koryVersion}"

    // Add any network engine you want (for the JVM, not required for native code).
    implementation "io.github.kychnoo:kory-ktor-cio:${koryVersion}"
    implementation "io.github.kychnoo:kory-ktor-okhttp:${koryVersion}"
}
```

Maven:
```maven
<project>
    ...
    <properties>
        <kory.version>0.0.1</kory.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.kychnoo</groupId>
            <artifactId>kory-core</artifactId>
            <version>${kory.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.kychnoo</groupId>
            <artifactId>kory-openai</artifactId>
            <version>${kory.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.kychnoo</groupId>
            <artifactId>kory-ktor</artifactId>
            <version>${kory.version}</version>
        </dependency>

        <!-- Add any network engine you want (for the JVM, not required for native code). -->
        <dependency>
            <groupId>io.github.kychnoo</groupId>
            <artifactId>kory-ktor-cio</artifactId>
            <version>${kory.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.kychnoo</groupId>
            <artifactId>kory-ktor-okhttp</artifactId>
            <version>${kory.version}</version>
        </dependency>
    </dependencies>
</project>
```

### Basic Usage
```kotlin
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import io.kory.core.chat.response.ChatResponse
import kotlinx.coroutines.runBlocking
import io.kory.core.chat.client.ApiKey

suspend fun main() {
    val apiKey = ApiKey.fromEnv("OPENAI_API_KEY") { e ->
        // This callback is triggered if the key was not found in the system environment.
        println("Received exception: ${e.message}, using fallback key...")
        "Put your fallback api key here" // Or throw an Exception
    }

    // Create an OpenAI-compatible client.
    // Works with any provider that exposes an OpenAI-compatible API.
    val client = OpenAIClient(
        apiKey = apiKey
    )

    // Send a catchable chat request using the DSL.
    // `chat` builds the message list, the client handles serialization and HTTP.
    client.chatCatching {
        chat("model-name") {
            user("Ping!")
        }
    }.onSuccess { response ->
        // Print the output message.
        println(response.outputText.ifEmpty { "No message" })
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
    onChunk { chunk: ChatChunk ->
        // Called on every chunk.
        println(chunk.text)
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

println(response.outputText)
```

#### or
```kotlin
// 1. Define a tool arguments.
@Serializable // Add kotlinx-serialization dependencies to your project.
data class WeatherToolArgs(
    @ToolParam(description = "City name")
    val city: String
)

val weatherTool = koryTool<WeatherToolArgs, String>(
    name = "get_weather",
    description = "Get weather for the city",
) { args ->
    "Sunny, 22°C in ${args.city}"
}

// 2. Use with automatic execution
val response = client.chatWithTools {
    registerTools { tool(weatherTool) }
    chat("model-name") {
        user("What's the weather in Moscow?")
    }
}

println(response.outputText)
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
./gradlew build         # Build all modules
./gradlew allTests     # Run all tests
./gradlew jvmTest     # Run JVM tests
```

## License
#### This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
