package io.kory.openai.files.dto

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class OpenAIFileListResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testFullDeserialization() {
        val raw = """
        {
          "object": "list",
          "data": [
            {
              "id": "file-1",
              "object": "file",
              "bytes": 100,
              "created_at": 1699892447,
              "filename": "a.jsonl",
              "purpose": "fine-tune"
            },
            {
              "id": "file-2",
              "object": "file",
              "bytes": 200,
              "created_at": 1699892500,
              "filename": "b.jsonl",
              "purpose": "assistants"
            }
          ],
          "first_id": "file-1",
          "last_id": "file-2",
          "has_more": true
        }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIFileListResponse>(raw)

        assertEquals("list", response.obj)
        assertEquals(2, response.data.size)
        assertEquals("file-1", response.data[0].id)
        assertEquals("b.jsonl", response.data[1].filename)
        assertEquals("file-1", response.firstId)
        assertEquals("file-2", response.lastId)
        assertTrue(response.hasMore)
    }

    @Test
    fun testMinimalDeserialization() {
        val response = json.decodeFromString<OpenAIFileListResponse>(
            """{"data":[],"has_more":false}"""
        )

        assertEquals("list", response.obj)
        assertTrue(response.data.isEmpty())
        assertNull(response.firstId)
        assertNull(response.lastId)
        assertFalse(response.hasMore)
    }

    @Test
    fun testPrintableOutputEmpty() {
        val response = json.decodeFromString<OpenAIFileListResponse>(
            """{"data":[],"has_more":false}"""
        )

        assertEquals("You don't have any uploads files", response.printableOutput())
    }

    @Test
    fun testPrintableOutputNonEmpty() {
        val response = json.decodeFromString<OpenAIFileListResponse>(
            """
            {
              "data": [
                {"id":"file-1","created_at":1,"filename":"a.jsonl","purpose":"fine-tune"},
                {"id":"file-2","created_at":2,"filename":"b.jsonl","purpose":"assistants"}
              ],
              "has_more": false
            }
            """.trimIndent()
        )

        assertEquals(
            "Your files(filename: fileId):\na.jsonl: file-1\nb.jsonl: file-2",
            response.printableOutput()
        )
    }

    @Test
    fun testRoundTrip() {
        val raw = """
        {
          "object": "list",
          "data": [
            {"id":"file-1","bytes":10,"created_at":5,"filename":"a.jsonl","purpose":"vision"}
          ],
          "first_id": "file-1",
          "last_id": "file-1",
          "has_more": false
        }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIFileListResponse>(raw)
        val decoded = json.decodeFromString(
            OpenAIFileListResponse.serializer(),
            json.encodeToString(OpenAIFileListResponse.serializer(), response)
        )

        assertEquals(response, decoded)
    }
}
