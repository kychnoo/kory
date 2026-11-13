package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Execute a shell command on the server (the `local_shell` tool action).
 *
 * @property type The type of the action. Always `"exec"`.
 * @property command The command to run, split into tokens.
 * @property timeoutMs Optional timeout in milliseconds for the command.
 * @property workingDirectory Optional working directory to run the command in.
 * @property env Environment variables to set for the command.
 * @property user Optional user to run the command as.
 *
 * @see io.kory.openai.responses.io.items.OpenAIResponsesLocalShellCall.action
 */
@Serializable
data class OpenAILocalShellExecAction(
    val type: String = "exec",
    val command: List<String>,
    @SerialName("timeout_ms") val timeoutMs: Long? = null,
    @SerialName("working_directory") val workingDirectory: String? = null,
    val env: Map<String, String> = emptyMap(),
    val user: String? = null,
)
