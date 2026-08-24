package examples.core.roles

import io.kory.core.chat.Chat
import io.kory.core.dsl.chat.koryChat
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content

fun processMessageRoles() {
    // All roles.

    // Now supported roles: Role.USER, Role.ASSISTANT, Role.SYSTEM, Role.TOOL)

    // Using roles in dsl-chat.
    val dslChat = koryChat(
        model = "gpt-5.6-sol"
    ) {
        // System role.
        system("You are helpful assistant.")

        // User role.
        user("Hello, What is the weather like in London?")

        // Tool role.
        tool("Current weather in London is Sunny.")

        // Assistant role.
        assistant("Hello, current weather in **London** is sunny. Have a nice day :)")
    }

    // Using roles in non-dsl chat.
    val chat = Chat(
        model = "gpt-5.6-sol",
        messages = listOf(
            Message(
                role = Role.SYSTEM,
                content = Content.Text("You are helpful assistant.")
            ),
            Message(
                role = Role.USER,
                content = Content.Text("Hello, What is the weather like in London?")
            ),
            Message(
                role = Role.TOOL,
                content = Content.ToolResult(
                    toolCallId = "tool_call_id_1",
                    name = "get_weather",
                    content = "Current weather in London is Sunny.")
            ),
            Message(
                role = Role.ASSISTANT,
                content = Content.Text("Hello, current weather in **London** is sunny. Have a nice day :)")
            )
        )
    )
}