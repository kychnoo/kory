package io.kory.openai.shared.param

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Processing tier used for serving an OpenAI chat completion request.
 *
 * When the parameter is set, the response body includes the `service_tier` value
 * based on the processing mode actually used, which may differ from the requested value.
 * When unset, the default behavior is [Auto].
 *
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionRequest.serviceTier
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionResponse.serviceTier
 */
@Serializable
@JvmInline
value class OpenAIServiceTier(val value: String) {
    companion object {
        /** Uses the service tier configured in the project settings. */
        @SerialName("auto") val Auto = OpenAIServiceTier("auto")

        /** Standard pricing and performance for the selected model. */
        @SerialName("default") val Default = OpenAIServiceTier("default")

        /** Flex Processing service tier with lower cost and slower processing. */
        @SerialName("flex") val Flex = OpenAIServiceTier("flex")

        /** Scale tier for provisioned throughput. */
        @SerialName("scale") val Scale = OpenAIServiceTier("scale")

        /** Priority processing with the fastest serving. */
        @SerialName("priority") val Priority = OpenAIServiceTier("priority")


    }

    object GroqSpecific {
        @SerialName("on_demand") val OnDemand = OpenAIServiceTier("on_demand")
        @SerialName("performance") val Performance = OpenAIServiceTier("performance")
        val Flex = OpenAIServiceTier.Flex
        val Auto = OpenAIServiceTier.Auto
    }

}