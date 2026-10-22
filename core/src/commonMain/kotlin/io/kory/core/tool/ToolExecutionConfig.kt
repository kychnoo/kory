package io.kory.core.tool

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

data class ToolExecutionConfig(
    val autoExecute: Boolean = true,
    val returnErrorsToAI: Boolean = true,
    val maxConcurrency: Int = 5,
    val executionDispatcher: CoroutineDispatcher = Dispatchers.IO,
    val onToolCall: ToolExecutionCallback? = null
)
