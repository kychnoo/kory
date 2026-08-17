package io.kory.utils

import kotlin.time.Instant
import kotlin.time.Clock
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@Serializable
class Printer(val message: String) {
    @OptIn(ExperimentalTime::class)
    fun printMessage() = runBlocking {
        val now: Instant = Clock.System.now()
        launch {
            delay(1000L.milliseconds)
            println(now.toString())
        }
        println(message)
    }
}