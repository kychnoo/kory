package examples.openai.client

import io.kory.core.chat.client.ApiKey
import io.kory.core.exception.KeyNotFoundException
import io.kory.ktor.KoryHttpClient
import io.kory.ktor.data.remote.auth.KoryAuth
import io.kory.ktor.data.remote.config.KoryHttpClientConfig
import io.kory.openai.chat.OpenAIClient

fun openAIClientCreation() {
    // Create OpenAI client.
    val client = try {
        OpenAIClient(apiKey = ApiKey.fromEnv()) // Set AI_API_KEY in your environment.
    } catch (kNfEx: KeyNotFoundException) {
        error("No api key provided.\nDetail message: ${kNfEx.message}")
    } catch (e: Exception) {
        error("Error: ${e.message}")
    }

    // With custom base url.
    val clientWithCustomUrl = try {
        OpenAIClient(apiKey = ApiKey.fromEnv())
    } catch (e: Exception) {
        error("Error: ${e.message}")
    }

    val baseUrl = "https://openai/compatible.domain/v1"
    val apiKey = ApiKey.fromEnv()

    // And with kory http client.
    val clientWithCustomHttpClient = OpenAIClient(
        apiKey = apiKey,
        baseUrl = baseUrl,
        httpClient = KoryHttpClient.create(
            KoryHttpClientConfig(
                baseUrl = baseUrl, // https://openai/compatible.domain/v1
                auth = KoryAuth.Bearer(apiKey.value) // Authentication
            )
        )
    )
}