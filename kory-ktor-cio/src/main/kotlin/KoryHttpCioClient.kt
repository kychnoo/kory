import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createKoryHttpCIOClient(
    baseUrl: String,
    auth: String? = null,
): KoryHttpClient =
    KoryHttpClient(
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
            }
//            install(Logging) {
//                level = LogLevel.ALL
//            }
            defaultRequest {
                url(baseUrl)

                header(HttpHeaders.ContentType, "application/json; charset=utf-8")
                auth?.let { key ->
                    header(
                        HttpHeaders.Authorization,
                        key
                    )
                }
            }
        }
    )