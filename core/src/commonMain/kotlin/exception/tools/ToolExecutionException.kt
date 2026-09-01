package io.kory.core.exception.tools

import io.kory.core.exception.KoryException

class ToolExecutionException(
    val toolName: String,
    val argumentsJson: String?,
    override val message: String,
    override val cause: Throwable? = null,
) : KoryException(message)