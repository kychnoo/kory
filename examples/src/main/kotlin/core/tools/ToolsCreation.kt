package examples.core.tools

import io.kory.core.dsl.chat.koryChat
import io.kory.core.dsl.chat.request.koryChatRequest
import io.kory.core.dsl.tool.koryTools
import io.kory.core.tool.KoryTool
import io.kory.core.tool.annotation.ToolParam
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class TestWeatherTool : KoryTool<TestWeatherTool.Args, String>(
    name = "get_weather", // Name for tool.
    description = "Get weather for the selected location" // Description for tool.
) {
    @Serializable // @Serializable from kotlinx.serialization for serialization custom data class.
    data class Args(
        @ToolParam(description = "Location for which weather information is needed") // Description about `location` field for AI.
        val location: String
    )

    override val argsSerializer: KSerializer<Args> = Args.serializer() // Use serializer from your Args data class or add your custom KSerializer.

    override suspend fun execute(args: Args): String { // Execute tool code here.
        return "Current weather in ${args.location} is sunny, 27°C"
    }
}

fun useTestWeatherToolInCode() {
    // Create tool instance.
    val getWeatherTool = TestWeatherTool()

    // Create a chat request with this tool.
    val request = koryChatRequest {
        // Register your tool.

        // With tools builder.
        registerTools {
            tool(getWeatherTool)
        }

        // Or without tools builder
        registerTools(getWeatherTool)

        chat(model = "gpt-5.6-sol") {
            user("Hello, what's the weather like in London?")
        }
    }

    // Registered tools automatically send to AI with request.
}

fun useTestWeatherToolInKoryTools() {
    // Create tool instance.
    val getWeatherTool = TestWeatherTool()

    // Create koryTools
    val tools = koryTools {
        tool(getWeatherTool) // Add getWeatherTool.
    }

    // Add tools to request.
    val requestWithKoryChat = koryChat(model = "gpt-5.6-sol") {
        user("Hello, what's the weather like in London?")
    }.asChatRequest(
        tools = tools
    )

    // Or with request.
    val requestWithKoryRequest = koryChatRequest {
        registerTools(tools)

        chat(model = "gpt-5.6-sol") {
            user("Hello, what's the weather like in London?")
        }
    }
}