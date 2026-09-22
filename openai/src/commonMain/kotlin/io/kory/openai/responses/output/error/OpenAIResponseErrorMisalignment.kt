package io.kory.openai.responses.output.error

import io.kory.openai.responses.model.error.OpenAIMisalignmentErrorType
import io.kory.openai.responses.model.error.OpenAIMisalignmentSteer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponseErrorMisalignment(
    @SerialName("detailed_explanation") val detailedExplanation: String? = null,
    @SerialName("error_type") val errorType: OpenAIMisalignmentErrorType? = null,
    val steer: OpenAIMisalignmentSteer? = null
)
