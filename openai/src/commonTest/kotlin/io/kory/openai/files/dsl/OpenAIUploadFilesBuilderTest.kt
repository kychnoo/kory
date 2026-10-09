package io.kory.openai.files.dsl

import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.files.KoryFile
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import io.kory.openai.files.model.OpenAIFilePurpose
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class OpenAIUploadFilesBuilderTest {

    private val createdPaths = mutableListOf<Path>()
    private var counter = 0

    @AfterTest
    fun cleanup() {
        for (path in createdPaths) {
            SystemFileSystem.delete(path, mustExist = false)
        }
        createdPaths.clear()
    }

    private fun createTempFile(): KoryFile {
        val path = Path(SystemTemporaryDirectory, "kory-uploads-test-${counter++}.txt")
        val file = KoryFile(path.toString())
        file.writeBytes("content".encodeToByteArray())
        createdPaths.add(path)
        return file
    }

    @Test
    fun testFileUsesDefaultPurpose() {
        val file = createTempFile()

        val requests = openAIUploadFilesRequest {
            file(file)
        }

        assertEquals(1, requests.size)
        assertEquals(OpenAIFilePurpose.FINE_TUNE, requests.single().purpose)
    }

    @Test
    fun testFileWithExplicitPurpose() {
        val file = createTempFile()

        val requests = openAIUploadFilesRequest {
            file(file, purpose = OpenAIFilePurpose.USER_DATA)
        }

        assertEquals(OpenAIFilePurpose.USER_DATA, requests.single().purpose)
    }

    @Test
    fun testDefaultPurposeIsApplied() {
        val first = createTempFile()
        val second = createTempFile()

        val requests = openAIUploadFilesRequest {
            defaultPurpose = OpenAIFilePurpose.ASSISTANTS
            file(first)
            file(second, purpose = OpenAIFilePurpose.USER_DATA)
        }

        assertEquals(OpenAIFilePurpose.ASSISTANTS, requests[0].purpose)
        assertEquals(OpenAIFilePurpose.USER_DATA, requests[1].purpose)
    }

    @Test
    fun testFileFromPath() {
        val file = createTempFile()

        val requests = openAIUploadFilesRequest {
            fileFromPath(file.filePath, purpose = OpenAIFilePurpose.ASSISTANTS)
        }

        assertEquals(file.filePath, requests.single().file.filePath)
        assertEquals(OpenAIFilePurpose.ASSISTANTS, requests.single().purpose)
    }

    @Test
    fun testFileWithMissingFileThrows() {
        assertFailsWith<FileNotFoundException> {
            openAIUploadFilesRequest {
                file(KoryFile("definitely/missing/file.txt"))
            }
        }
    }

    @Test
    fun testNestedRequestDsl() {
        val file = createTempFile()

        val requests = openAIUploadFilesRequest {
            request(purpose = OpenAIFilePurpose.USER_DATA) {
                file(file)
            }
        }

        assertEquals(1, requests.size)
        assertEquals(OpenAIFilePurpose.USER_DATA, requests.single().purpose)
        assertEquals(file.filePath, requests.single().file.filePath)
    }

    @Test
    fun testPrebuiltRequestAndRequests() {
        val file = createTempFile()
        val prebuilt = OpenAIUploadFileRequest(file, OpenAIFilePurpose.ASSISTANTS)
        val another = OpenAIUploadFileRequest(createTempFile(), OpenAIFilePurpose.USER_DATA)

        val requests = openAIUploadFilesRequest {
            request(prebuilt)
            requests(listOf(another))
        }

        assertEquals(listOf(prebuilt, another), requests)
    }

    @Test
    fun testVarargRequests() {
        val first = OpenAIUploadFileRequest(createTempFile(), OpenAIFilePurpose.ASSISTANTS)
        val second = OpenAIUploadFileRequest(createTempFile(), OpenAIFilePurpose.USER_DATA)

        val requests = openAIUploadFilesRequest {
            requests(first, second)
        }

        assertEquals(listOf(first, second), requests)
    }

    @Test
    fun testDuplicateRequestsAreMerged() {
        val file = createTempFile()
        val request = OpenAIUploadFileRequest(file, OpenAIFilePurpose.FINE_TUNE)

        val requests = openAIUploadFilesRequest {
            request(request)
            request(request)
        }

        assertEquals(1, requests.size)
    }

    @Test
    fun testEmptyBuilderProducesEmptyList() {
        assertTrue(openAIUploadFilesRequest { }.isEmpty())
    }
}
