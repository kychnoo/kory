package io.kory.openai.samples.responses.tool

import io.kory.core.tool.annotation.ToolParam
import io.kory.openai.responses.io.tool.OpenAIFunctionTool
import io.kory.openai.responses.io.tool.dsl.openAIFunctionTool
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

fun createToolUsingDsl() {
    // Creating a data class for tool arguments
    @Serializable
    data class CreateTaskArgs(
        @ToolParam(description = "Title of the task to be saved") val taskName: String,
        @ToolParam(description = "Description of the task to be saved") val taskDescription: String,
    )

    // Create a tool using dsl.
    val createTaskTool = openAIFunctionTool<CreateTaskArgs, String>(
        name = "create_task",
        description = "Create a new task",
    ) { args ->
        // Save task...
        "Task with name ${args.taskName} successfully created!"
    }

    // Send request to OpenAI or use...
}

fun createToolIUsingAbstractClass() {
    @Serializable
    data class CreateTaskToolArgs(
        @ToolParam(description = "Title of the task to be saved") val taskName: String,
        @ToolParam(description = "Description of the task to be saved") val taskDescription: String,
    )

    class CreateTaskTool() : OpenAIFunctionTool<CreateTaskToolArgs, String>(
        name = "create_task",
        description = "Create a new task",
    ) {
        // Get serializers for args and result.
        override val argsSerializer: KSerializer<CreateTaskToolArgs> = serializer()
        override val resultSerializer: KSerializer<String> = serializer()

        override suspend fun execute(args: CreateTaskToolArgs): String {
            // Save task...
            return "Task with name ${args.taskName} successfully created!"
        }
    }

    // Use CreateTaskTool...
}