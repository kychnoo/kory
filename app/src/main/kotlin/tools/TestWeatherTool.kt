package io.kory.app.tools

import io.kory.core.tool.KoryTool
import io.kory.core.tool.annotation.ToolParam
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

class TestWeatherTool : KoryTool<TestWeatherTool.Args, String>(
    name = "get_weather",
    description = "Get weather in selected city",
) {
    @Serializable
    data class Args(
        @ToolParam(description = "Selected city")
        val city: String
    )

    override val argsSerializer: KSerializer<Args> = serializer()

    override suspend fun execute(args: Args): String {
        return "Current weather in ${args.city} is sunny, 22 Celsius"
    }
}