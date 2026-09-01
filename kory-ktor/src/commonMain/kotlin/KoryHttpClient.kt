package io.kory.ktor

import io.kory.ktor.data.remote.KoryHttpResponse
import io.kory.ktor.data.remote.auth.KoryAuth
import io.kory.ktor.data.remote.config.KoryHttpClientConfig
import io.kory.ktor.data.remote.discovery.discoverKoryHttpEngineFactory
import io.kory.ktor.exception.KoryHttpException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.readRawBytes
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

private const val DEFAULT_BUFFER_SIZE = 8096

/**
 * HTTP client wrapper for Kory, providing POST, GET, and streaming POST methods.
 *
 * Create instances via [KoryHttpClient.create] with a [KoryHttpClientConfig].
 * The engine is discovered automatically from the classpath (e.g. via `kory-ktor-cio`)
 * or can be specified explicitly in the config.
 *
 * @property client The underlying Ktor [HttpClient].
 *
 * @sample examples.kory-ktor.HttpClientCreation
 */
class KoryHttpClient private constructor(
    val client: HttpClient
) {
    /**
     * Sends a POST request with a string body.
     *
     * @param path The URL path (appended to the base URL).
     * @param body The request body as a string.
     * @return A [KoryHttpResponse] with status and body.
     * @throws KoryHttpException.HttpStatus if the response status is not 2xx.
     * @throws KoryHttpException.Timeout if the request times out.
     * @throws KoryHttpException.Network if a network error occurs.
     */
    suspend fun post(path: String, body: String): KoryHttpResponse = runCatching {
        client.post(path) { setBody(body) }
    }.mapCatching { response ->
        response.toKoryHttpResponseOrThrow()
    }.getOrElse { throw it.toKoryHttpException(path) }



    /**
     * Sends a streaming POST request, emitting response lines as they arrive.
     *
     * Lines are trimmed and emitted individually. Empty lines are skipped.
     *
     * @param path The URL path (appended to the base URL).
     * @param body The request body as a string.
     * @return A [Flow] of trimmed response lines.
     * @throws KoryHttpException.HttpStatus if the response status is not 2xx.
     */
    fun streamPost(path: String, body: String): Flow<String> = channelFlow {
        client.preparePost(path) {
            setBody(body)
        }.execute { response ->
            if (!response.status.isSuccess()) {
                throw KoryHttpException.HttpStatus(
                    status = response.status.value,
                    body = response.readRawBytes(),
                    url = path
                )
            }

            val channel: ByteReadChannel = response.bodyAsChannel()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val builder = StringBuilder()

            while (!channel.isClosedForRead) {
                currentCoroutineContext().ensureActive()
                val read = channel.readAvailable(buffer)
                if (read == -1) break
                if (read == 0) continue

                builder.append(buffer.decodeToString(0, read))


                val lines = builder.split('\n')
                for (line in lines.dropLast(1)) {
                    val trimmedLine = line.trim()
                    if (trimmedLine.isNotEmpty()) send(trimmedLine)
                }

                builder.setLength(0)
                builder.append(lines.last())
            }

            if (builder.isNotBlank()) {
                send(builder.toString().trim())
            }
        }
    }

    /**
     * Sends a GET request.
     *
     * @param path The URL path (appended to the base URL).
     * @return A [KoryHttpResponse] with status and body.
     * @throws KoryHttpException.HttpStatus if the response status is not 2xx.
     * @throws KoryHttpException.Timeout if the request times out.
     * @throws KoryHttpException.Network if a network error occurs.
     */
    suspend fun get(path: String): KoryHttpResponse = runCatching {
        client.get(path)
    }.mapCatching { response ->
        response.toKoryHttpResponseOrThrow()
    }.getOrElse { throw it.toKoryHttpException(path) }

    private suspend fun HttpResponse.toKoryHttpResponseOrThrow(): KoryHttpResponse {
        if (!status.isSuccess()) {
            throw KoryHttpException.HttpStatus(
                status = status.value,
                body = readRawBytes(),
                url = request.url.toString()
            )
        }

        return KoryHttpResponse(
            status = status.value,
            body = body()
        )
    }

    private fun Throwable.toKoryHttpException(url: String): Throwable = when (this) {
        is KoryHttpException -> this
        is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> KoryHttpException.Timeout(
            "Request to url $url timed out", this
        )
        is IOException -> KoryHttpException.Network("Network failure calling $url", this)
        else -> this
    }

    companion object {
        /**
         * Creates a [KoryHttpClient] from a [KoryHttpClientConfig].
         *
         * The HTTP engine is either:
         * - Provided via [KoryHttpClientConfig.engineFactory].
         * - Discovered automatically from the classpath via [discoverKoryHttpEngineFactory].
         *
         * @param config The client configuration.
         * @return A configured [KoryHttpClient].
         * @throws IllegalStateException if no engine factory is found.
         */
        fun create(config: KoryHttpClientConfig): KoryHttpClient {
            val factory = config.engineFactory ?: discoverKoryHttpEngineFactory()
            ?: error(
                "No Kory HTTP engine found. On JVM: add `kory-ktor-cio` dependency. " +
                "On Native: the engine should be registered automatically - " +
                "if not, call KoryEngineRegistry.register(...) before creating the client " +
                "or pass `engineFactory` explicitly in KoryHttpClientConfig."
            )

            val engine = factory.create()

            val httpClient = HttpClient(engine) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = config.requestTimeoutMillis
                }
                if(config.logging) {
                    install(Logging) { level = LogLevel.ALL }
                }
                defaultRequest {
                    url(config.baseUrl)
                    header(HttpHeaders.ContentType, "application/json; charset=utf-8")
                    applyAuth(config.auth)
                    config.extraHeaders.forEach { (key, value) -> header(key, value) }
                }
            }

            return KoryHttpClient(httpClient)
        }

        private fun DefaultRequest.DefaultRequestBuilder.applyAuth(auth: KoryAuth) {
            when (auth) {
                is KoryAuth.None -> Unit
                is KoryAuth.Bearer -> header(HttpHeaders.Authorization, "Bearer ${auth.token}")
                is KoryAuth.ApiKeyHeader -> header(auth.headerName, auth.key)
                is KoryAuth.Custom -> auth.headers.forEach { (key, value) -> header(key, value) }
            }
        }
    }
}