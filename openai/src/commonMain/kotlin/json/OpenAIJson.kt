package io.kory.openai.json

import kotlinx.serialization.json.Json

internal val json = Json {
    ignoreUnknownKeys = true
}