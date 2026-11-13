package io.kory.openai.responses.serialization

import io.kory.core.message.Role
import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.items.OpenAIResponsesCodeInterpreterCall
import io.kory.openai.responses.io.items.OpenAIResponsesComputerCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesCustomToolCall
import io.kory.openai.responses.io.items.OpenAIResponsesCustomToolCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesEasyInputMessage
import io.kory.openai.responses.io.items.OpenAIResponsesImageGenerationCall
import io.kory.openai.responses.io.items.OpenAIResponsesItemReference
import io.kory.openai.responses.io.items.OpenAIResponsesLocalShellCall
import io.kory.openai.responses.io.items.OpenAIResponsesLocalShellCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesMcpApprovalRequest
import io.kory.openai.responses.io.items.OpenAIResponsesMcpApprovalResponse
import io.kory.openai.responses.io.items.OpenAIResponsesMcpCall
import io.kory.openai.responses.io.items.OpenAIResponsesMessage
import io.kory.openai.responses.io.items.OpenAIResponsesOutputMessage
import io.kory.openai.responses.io.items.OpenAIResponsesWebSearchCall
import io.kory.openai.responses.io.items.function.OpenAIFunctionCallOutputOutput
import io.kory.openai.responses.message.content.OpenAIResponseContent
import io.kory.openai.responses.message.content.OpenAIResponseOutputContent
import io.kory.openai.responses.model.OpenAIWebSearchAction
import io.kory.openai.responses.model.OpenAIWebSearchStatus
import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class OpenAIResponseInputItemSerializerTest {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    /* ====================== message disambiguation ====================== */

    @Test
    fun testAssistantMessageWithIdDecodesToOutputMessage() {
        val item = decode(
            """{"type":"message","id":"msg_1","role":"assistant",
                "content":[{"type":"output_text","text":"hi"}],"status":"completed"}"""
        )
        val outputMessage = assertIs<OpenAIResponsesOutputMessage>(item)
        assertEquals("msg_1", outputMessage.id)
        assertEquals(Role.Assistant, outputMessage.role)
        assertEquals(OpenAIStatus.COMPLETED, outputMessage.status)
    }

    @Test
    fun testAssistantMessageWithoutIdDecodesToEasyInputMessage() {
        val item = decode("""{"type":"message","role":"assistant","content":"hello"}""")
        val easy = assertIs<OpenAIResponsesEasyInputMessage>(item)
        assertEquals(Role.Assistant, easy.role)
        assertEquals(OpenAIResponseContent.Text("hello"), easy.content)
    }

    @Test
    fun testUserMessageWithPrimitiveContentDecodesToEasyInputMessage() {
        val item = decode("""{"type":"message","role":"user","content":"plain text"}""")
        val easy = assertIs<OpenAIResponsesEasyInputMessage>(item)
        assertEquals(Role.User, easy.role)
    }

    @Test
    fun testUserMessageWithListContentDecodesToMessage() {
        val item = decode(
            """{"type":"message","role":"user",
                "content":[{"type":"input_text","text":"hello"}]}"""
        )
        val message = assertIs<OpenAIResponsesMessage>(item)
        assertEquals(Role.User, message.role)
    }

    @Test
    fun testMessageWithStatusDecodesToMessage() {
        val item = decode(
            """{"type":"message","role":"user","content":"hello","status":"completed"}"""
        )
        assertIs<OpenAIResponsesMessage>(item)
    }

    /* ====================== tool items ====================== */

    @Test
    fun testFunctionCall() {
        val item = decode(
            """{"type":"function_call","id":"fc_1","call_id":"call_1","name":"f","arguments":"{}"}"""
        )
        assertEquals("f", assertIs<io.kory.openai.responses.io.items.OpenAIResponsesFunctionCall>(item).name)
    }

    @Test
    fun testFunctionCallOutputStringForm() {
        val item = decode("""{"type":"function_call_output","call_id":"call_1","output":"42"}""")
        val output = assertIs<io.kory.openai.responses.io.items.OpenAIResponsesFunctionCallOutput>(item)
        assertEquals(OpenAIFunctionCallOutputOutput.StringOutput("42"), output.output)
    }

    @Test
    fun testFunctionCallOutputListForm() {
        val item = decode(
            """{"type":"function_call_output","call_id":"call_1",
                "output":[{"type":"message","role":"user","content":"nested"}]}"""
        )
        val output = assertIs<io.kory.openai.responses.io.items.OpenAIResponsesFunctionCallOutput>(item)
        val list = assertIs<OpenAIFunctionCallOutputOutput.OutputContentList>(output.output)
        assertEquals(1, list.items.size)
        // Primitive string content inside a nested message decodes to the EasyInputMessage form.
        assertIs<OpenAIResponsesEasyInputMessage>(list.items.first())
    }

    @Test
    fun testReasoning() {
        val item = decode(
            """{"type":"reasoning","id":"rs_1","summary":[{"type":"summary_text","text":"thinking"}],
                "content":[{"type":"reasoning_text","text":"raw","signature":"sig"}]}"""
        )
        assertEquals(1, assertIs<io.kory.openai.responses.io.items.OpenAIResponsesReasoning>(item).summary.size)
    }

    @Test
    fun testFileSearchCall() {
        val item = decode(
            """{"type":"file_search_call","id":"fs_1","queries":["q"],"status":"completed"}"""
        )
        assertIs<io.kory.openai.responses.io.items.OpenAIResponsesFileSearchCall>(item)
    }

    @Test
    fun testMcpListTools() {
        val item = decode(
            """{"type":"mcp_list_tools","id":"ml_1","server_label":"srv","tools":[]}"""
        )
        assertEquals("srv", assertIs<io.kory.openai.responses.io.items.OpenAIResponseMcpListTools>(item).serverLabel)
    }

    @Test
    fun testComputerCall() {
        val item = decode(
            """{"type":"computer_call","id":"cc_1","call_id":"call_1","pending_safety_checks":[]}"""
        )
        assertIs<io.kory.openai.responses.io.items.OpenAIResponsesComputerCall>(item)
    }

    @Test
    fun testComputerCallOutputString() {
        val item = decode("""{"type":"computer_call_output","call_id":"call_1","output":"ok"}""")
        val output = assertIs<OpenAIResponsesComputerCallOutput>(item)
        assertEquals(
            io.kory.openai.responses.io.items.OpenAIResponsesComputerCallResult.StringOutput("ok"),
            output.output,
        )
    }

    @Test
    fun testComputerCallOutputScreenshot() {
        val item = decode(
            """{"type":"computer_call_output","call_id":"call_1",
                "output":{"type":"computer_screenshot","image_url":"https://example.com/s.png"}}"""
        )
        val output = assertIs<OpenAIResponsesComputerCallOutput>(item)
        val screenshot = assertIs<io.kory.openai.responses.io.items.OpenAIResponsesComputerCallResult.ComputerScreenshot>(output.output)
        assertEquals("https://example.com/s.png", screenshot.imageUrl)
    }

    @Test
    fun testWebSearchCall() {
        val item = decode(
            """{"type":"web_search_call","id":"ws_1","status":"completed",
                "action":{"type":"search","queries":["kotlin kmp"]}}"""
        )
        val call = assertIs<OpenAIResponsesWebSearchCall>(item)
        assertEquals(OpenAIWebSearchStatus.COMPLETED, call.status)
        val action = assertIs<OpenAIWebSearchAction.Search>(call.action)
        assertEquals(listOf("kotlin kmp"), action.queries)
    }

    @Test
    fun testWebSearchCallOpenPageAction() {
        val item = decode(
            """{"type":"web_search_call","id":"ws_2","status":"completed",
                "action":{"type":"open_page","url":"https://example.com"}}"""
        )
        val call = assertIs<OpenAIResponsesWebSearchCall>(item)
        assertEquals("https://example.com", assertIs<OpenAIWebSearchAction.OpenPage>(call.action).url)
    }

    @Test
    fun testItemReference() {
        val item = decode("""{"type":"item_reference","id":"it_1"}""")
        assertEquals("it_1", assertIs<OpenAIResponsesItemReference>(item).id)
    }

    @Test
    fun testMcpCall() {
        val item = decode(
            """{"type":"mcp_call","id":"mc_1","server_label":"srv","name":"tool","arguments":"{}",
                "output":"done","approval_request_id":"ar_1"}"""
        )
        val call = assertIs<OpenAIResponsesMcpCall>(item)
        assertEquals("srv", call.serverLabel)
        assertEquals("done", call.output)
        assertEquals("ar_1", call.approvalRequestId)
    }

    @Test
    fun testMcpApprovalRequest() {
        val item = decode(
            """{"type":"mcp_approval_request","id":"ar_1","server_label":"srv","name":"tool","arguments":"{}"}"""
        )
        assertIs<OpenAIResponsesMcpApprovalRequest>(item)
    }

    @Test
    fun testMcpApprovalResponse() {
        val item = decode(
            """{"type":"mcp_approval_response","id":"mr_1","approval_request_id":"ar_1","approve":true,
                "reason":"looks safe"}"""
        )
        val response = assertIs<OpenAIResponsesMcpApprovalResponse>(item)
        assertTrue(response.approve)
        assertEquals("looks safe", response.reason)
    }

    @Test
    fun testImageGenerationCall() {
        val item = decode(
            """{"type":"image_generation_call","id":"ig_1","status":"completed","result":"base64img",
                "size":"1024x1024","revised_prompt":"a better prompt"}"""
        )
        val call = assertIs<OpenAIResponsesImageGenerationCall>(item)
        assertEquals("base64img", call.result)
        assertEquals("1024x1024", call.size)
        assertEquals("a better prompt", call.revisedPrompt)
    }

    @Test
    fun testCodeInterpreterCall() {
        val item = decode(
            """{"type":"code_interpreter_call","id":"ci_1","status":"completed","container_id":"ctn",
                "code":"1+1","outputs":[{"type":"logs","logs":"out"}]}"""
        )
        val call = assertIs<OpenAIResponsesCodeInterpreterCall>(item)
        assertEquals("ctn", call.containerId)
        assertEquals(1, call.outputs?.size)
    }

    @Test
    fun testCodeInterpreterCallImageOutput() {
        val item = decode(
            """{"type":"code_interpreter_call","id":"ci_2","status":"completed","container_id":"ctn",
                "outputs":[{"type":"image","url":"https://example.com/img.png"}]}"""
        )
        val call = assertIs<OpenAIResponsesCodeInterpreterCall>(item)
        assertEquals("https://example.com/img.png", call.outputs?.first()?.let {
            assertIs<io.kory.openai.responses.io.items.OpenAIResponsesCodeInterpreterOutput.Image>(it).url
        })
    }

    @Test
    fun testCustomToolCall() {
        val item = decode(
            """{"type":"custom_tool_call","id":"ct_1","call_id":"call_1","name":"tool","input":"raw"}"""
        )
        val call = assertIs<OpenAIResponsesCustomToolCall>(item)
        assertEquals("raw", call.input)
    }

    @Test
    fun testCustomToolCallOutput() {
        val item = decode("""{"type":"custom_tool_call_output","call_id":"call_1","output":"result"}""")
        val output = assertIs<OpenAIResponsesCustomToolCallOutput>(item)
        assertEquals(OpenAIFunctionCallOutputOutput.StringOutput("result"), output.output)
    }

    @Test
    fun testLocalShellCall() {
        val item = decode(
            """{"type":"local_shell_call","id":"ls_1","call_id":"call_1",
                "action":{"type":"exec","command":["echo","hi"]}}"""
        )
        val call = assertIs<OpenAIResponsesLocalShellCall>(item)
        assertEquals(listOf("echo", "hi"), call.action.command)
    }

    @Test
    fun testLocalShellCallOutput() {
        val item = decode("""{"type":"local_shell_call_output","call_id":"call_1","output":"done"}""")
        val output = assertIs<OpenAIResponsesLocalShellCallOutput>(item)
        assertEquals("done", output.output)
    }

    /* ====================== errors ====================== */

    @Test
    fun testUnknownTypeThrows() {
        val exception = kotlin.runCatching {
            decode("""{"type":"unknown_item","id":"x"}""")
        }.exceptionOrNull()

        assertTrue(exception is SerializationException)
        assertTrue(exception.message!!.contains("Unknown type"))
    }

    @Test
    fun testMissingDiscriminatorThrows() {
        val exception = kotlin.runCatching {
            decode("""{"id":"x","role":"user"}""")
        }.exceptionOrNull()

        assertTrue(exception is SerializationException)
        assertTrue(exception.message!!.contains("No discriminator"))
    }

    /* ====================== serialize: type field injection + round trip ====================== */

    @Test
    fun testSerializationInjectsTypeField() {
        val encoded = json.encodeToString(
            OpenAIResponseInputItem.serializer(),
            OpenAIResponsesItemReference(id = "it_1") as OpenAIResponseInputItem,
        )
        assertTrue(encoded.contains("\"type\":\"item_reference\""))
        assertTrue(encoded.contains("\"id\":\"it_1\""))
    }

    @Test
    fun testMessageRoundTrip() {
        val item: OpenAIResponseInputItem = OpenAIResponsesOutputMessage(
            id = "msg_rt",
            content = listOf(OpenAIResponseOutputContent.ResponseOutputText("round trip")),
            status = OpenAIStatus.COMPLETED,
        )

        val decoded = roundTrip(item)
        val outputMessage = assertIs<OpenAIResponsesOutputMessage>(decoded)
        assertEquals("msg_rt", outputMessage.id)
        assertEquals("round trip", outputMessage.content.first().printableContent)
    }

    @Test
    fun testEasyInputMessageRoundTrip() {
        val item: OpenAIResponseInputItem = OpenAIResponsesEasyInputMessage(
            role = Role.User,
            content = OpenAIResponseContent.Text("easy"),
        )

        val decoded = roundTrip(item)
        val easy = assertIs<OpenAIResponsesEasyInputMessage>(decoded)
        assertEquals(Role.User, easy.role)
        assertEquals(OpenAIResponseContent.Text("easy"), easy.content)
    }

    @Test
    fun testComputerCallOutputRoundTrip() {
        val item: OpenAIResponseInputItem = OpenAIResponsesComputerCallOutput(
            callId = "call_rt",
            output = io.kory.openai.responses.io.items.OpenAIResponsesComputerCallResult.ComputerScreenshot(
                fileId = "file_1",
            ),
        )

        val decoded = roundTrip(item)
        val output = assertIs<OpenAIResponsesComputerCallOutput>(decoded)
        val screenshot = assertIs<io.kory.openai.responses.io.items.OpenAIResponsesComputerCallResult.ComputerScreenshot>(output.output)
        assertEquals("file_1", screenshot.fileId)
    }

    @Test
    fun testWebSearchCallRoundTrip() {
        val item: OpenAIResponseInputItem = OpenAIResponsesWebSearchCall(
            id = "ws_rt",
            status = OpenAIWebSearchStatus.COMPLETED,
            action = OpenAIWebSearchAction.FindInPage(url = "https://example.com", pattern = "kmp"),
        )

        val decoded = roundTrip(item)
        val call = assertIs<OpenAIResponsesWebSearchCall>(decoded)
        assertEquals(OpenAIWebSearchAction.FindInPage("https://example.com", "kmp"), call.action)
    }

    private fun decode(jsonStr: String): OpenAIResponseInputItem =
        json.decodeFromString(OpenAIResponseInputItem.serializer(), jsonStr.trimIndent())

    private fun roundTrip(item: OpenAIResponseInputItem): OpenAIResponseInputItem {
        val encoded = json.encodeToString(OpenAIResponseInputItem.serializer(), item)
        return decode(encoded)
    }
}
