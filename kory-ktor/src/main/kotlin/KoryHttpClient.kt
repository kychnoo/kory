import data.remote.KoryHttpResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KoryHttpClient(
    val client: HttpClient
) {
    suspend fun post(url: String, body: String): KoryHttpResponse {
        val response = client.post(url) {
            setBody(body)
        }

        return KoryHttpResponse(
            status = response.status.value,
            body = response.body()
        )
    }

    fun streamPost(url: String, body: String): Flow<String> = flow {
        client.preparePost(url) {
            setBody(body)
        }.execute() { response ->
            val channel: ByteReadChannel = response.bodyAsChannel()

            val byffer = ByteArray(4096)
            val builder = StringBuilder()

            while (!channel.isClosedForRead) {
                val read = channel.readAvailable(byffer)
                if (read <= 0) continue

                val chunk = String(byffer, 0, read, Charsets.UTF_8)
                builder.append(chunk)

                val lines = builder.split('\n')

                for (line in lines.dropLast(1)) {
                    emit(line.trim())
                }

                builder.clear()
                builder.append(lines.last())
            }

            if (builder.isNotBlank()) {
                emit(builder.toString())
            }
        }
    }

    suspend fun get(url: String): KoryHttpResponse {
        val response = client.get(url)
        return KoryHttpResponse(
            status = response.status.value,
            body = response.body()
        )
    }
}