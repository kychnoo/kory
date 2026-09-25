package io.kory.openai.files.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The intended purpose of an uploaded file in the OpenAI Files API.
 *
 * @property value The string value sent to the API.
 */
@Serializable
enum class OpenAIFilePurpose(val value: String) {
    @SerialName("fine-tune")
    FINE_TUNE("fine-tune"),

    @SerialName("fine-tune-results")
    FINE_TUNE_RESULTS("fine-tune-results"),

    @SerialName("assistants")
    ASSISTANTS("assistants"),

    @SerialName("assistants_output")
    ASSISTANTS_OUTPUT("assistants_output"),

    @SerialName("user_data")
    USER_DATA("user_data")
}
