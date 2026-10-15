package io.kory.ktor.shared.serialization

import kotlinx.serialization.json.Json

@PublishedApi
internal val json = Json {
    ignoreUnknownKeys = true
}