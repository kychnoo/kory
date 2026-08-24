package io.kory.core.dsl.marker

/**
 * DSL marker annotation for the Kory chat builder DSL.
 *
 * Prevents accidental access to outer scope receivers in nested DSL lambdas.
 * All Kory DSL builders ([io.kory.core.dsl.chat.ChatBuilder], [io.kory.core.dsl.chat.request.ChatRequestBuilder], [io.kory.core.dsl.content.ContentBuilder],
 * [io.kory.core.dsl.tool.ToolsBuilder]) are annotated with this.
 */
@DslMarker
annotation class KoryDsl