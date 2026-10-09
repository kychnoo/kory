package io.kory.openai.responses.model.error

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class OpenAIMisalignmentErrorType(val value: String) {
    object SafetyAlertErrorType {
        val potentiallyUnintendedDataTransfer = OpenAIMisalignmentErrorType("potentially_unintended_data_transfer")
        val potentiallyUnintendedDataAccess = OpenAIMisalignmentErrorType("potentially_unintended_data_access")
        val potentiallyUnintendedDestructiveActivity = OpenAIMisalignmentErrorType("potentially_unintended_destructive_activity")
        val other = OpenAIMisalignmentErrorType("other")
    }
}