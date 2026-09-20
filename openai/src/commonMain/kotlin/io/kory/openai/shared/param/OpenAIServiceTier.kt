package io.kory.openai.shared.param

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Processing tier used for serving an OpenAI chat completion request.
 *
 * When the parameter is set, the response body includes the `service_tier` value
 * based on the processing mode actually used, which may differ from the requested value.
 * When unset, the default behavior is [AUTO].
 *
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionRequest.serviceTier
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionResponse.serviceTier
 */
@Serializable
enum class OpenAIServiceTier {
    /** Uses the service tier configured in the project settings. */
    @SerialName("auto")
    AUTO,

    /** Standard pricing and performance for the selected model. */
    @SerialName("default")
    DEFAULT,

    /** Flex Processing service tier with lower cost and slower processing. */
    @SerialName("flex")
    FLEX,

    /** Scale tier for provisioned throughput. */
    @SerialName("scale")
    SCALE,

    /** Priority processing with the fastest serving. */
    @SerialName("priority")
    PRIORITY,
}