package io.kory.core.dsl.marker

/**
 * DSL marker annotation for the Kory chat builder DSL.
 *
 * Prevents accidental access to outer scope receivers in nested DSL lambdas.
 * All Kory DSL builders ([ChatBuilder], [ChatRequestBuilder], [ContentBuilder],
 * [ToolsBuilder]) are annotated with this.
 */
@DslMarker
annotation class KoryDsl