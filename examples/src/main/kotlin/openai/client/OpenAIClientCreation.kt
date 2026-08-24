package examples.openai.client

import io.kory.ktor.KoryHttpClient
import io.kory.ktor.data.remote.auth.KoryAuth
import io.kory.ktor.data.remote.config.KoryHttpClientConfig
import io.kory.openai.chat.OpenAIClient

fun openAIClientCreation() {
    val openAIApiKey = System.getenv("OPEN_AI_API_KEY")

    if (openAIApiKey.isNullOrBlank()) error("Error: No api key provided")

    // Create OpenAI client.
    val client = OpenAIClient(
        apiKey = openAIApiKey // Api key(required)
    )

    // With custom base url.
    val clientWithCustomUrl = OpenAIClient(
        apiKey = "Your api key",
        baseUrl = "https://openai/compatible.domain/v1",
    )

    val baseUrl = "https://openai/compatible.domain/v1"
    val apiKey = "Your API key"

    // And with kory http client.
    val clientWithCustomHttpClient = OpenAIClient(
        apiKey = apiKey,
        baseUrl = baseUrl,
        httpClient = KoryHttpClient.create(
            KoryHttpClientConfig(
                baseUrl = baseUrl, // https://openai/compatible.domain/v1
                auth = KoryAuth.Bearer(apiKey) // Authentication(Optional)
            )

        )
    )
}