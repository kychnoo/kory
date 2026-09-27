package io.kory.openai.files.dsl

import io.kory.core.exception.files.FileAlreadyExistsException
import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.exception.files.upload.FileToUploadNotSelectedException
import io.kory.core.files.KoryFile
import io.kory.openai.files.model.OpenAIFilePurpose
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class OpenAIUploadFileRequestBuilderTest {

    private val createdPaths = mutableListOf<Path>()
    private var counter = 0

    @AfterTest
    fun cleanup() {
        for (path in createdPaths) {
            SystemFileSystem.delete(path, mustExist = false)
        }
        createdPaths.clear()
    }

    private fun createTempFile(content: String = "hello"): KoryFile {
        val path = Path(SystemTemporaryDirectory, "kory-upload-test-${counter++}.txt")
        val file = KoryFile(path.toString())
        file.writeBytes(content.encodeToByteArray())
        createdPaths.add(path)
        return file
    }

    @Test
    fun testBuildWithFileUsesDefaultPurpose() {
        val file = createTempFile()

        val request = openAIUploadFileRequest {
            file(file)
        }

        assertEquals(file.filePath, request.file.filePath)
        assertEquals(OpenAIFilePurpose.FINE_TUNE, request.purpose)
        assertEquals(null, request.expiresAfter)
    }

    @Test
    fun testBuildWithExplicitPurpose() {
        val file = createTempFile()

        val request = openAIUploadFileRequest(purpose = OpenAIFilePurpose.ASSISTANTS) {
            file(file)
        }

        assertEquals(OpenAIFilePurpose.ASSISTANTS, request.purpose)
    }

    @Test
    fun testBuildWithoutFileThrows() {
        assertFailsWith<FileToUploadNotSelectedException> {
            openAIUploadFileRequest {
                // No file set.
            }
        }
    }

    @Test
    fun testFileWithMissingFileThrows() {
        assertFailsWith<FileNotFoundException> {
            openAIUploadFileRequest {
                file(KoryFile("definitely/missing/file.txt"))
            }
        }
    }

    @Test
    fun testFileFromPathLoadsExistingFile() {
        val file = createTempFile()

        val request = openAIUploadFileRequest {
            fileFromPath(file.filePath)
        }

        assertEquals(file.filePath, request.file.filePath)
        assertTrue(request.file.exists())
    }

    @Test
    fun testFileFromPathWithMissingPathThrows() {
        assertFailsWith<FileNotFoundException> {
            openAIUploadFileRequest {
                fileFromPath("definitely/missing/file.txt")
            }
        }
    }

    @Test
    fun testCreateFileCreatesAndSetsFile() {
        val path = Path(SystemTemporaryDirectory, "kory-upload-created-${counter++}.txt")
        createdPaths.add(path)

        val request = openAIUploadFileRequest {
            createFile(path.toString()) { created ->
                created.writeBytes("created-content".encodeToByteArray())
            }
        }

        assertEquals(path.toString(), request.file.filePath)
        assertTrue(request.file.exists())
        assertEquals("created-content", request.file.readBytes().decodeToString())
    }

    @Test
    fun testCreateFileExistingWithoutRewriteThrows() {
        val file = createTempFile()

        assertFailsWith<FileAlreadyExistsException> {
            openAIUploadFileRequest {
                createFile(file.filePath) { }
            }
        }
    }

    @Test
    fun testCreateFileExistingWithRewriteOverwrites() {
        val file = createTempFile("old")

        val request = openAIUploadFileRequest {
            createFile(file.filePath, rewriteExists = true) { created ->
                created.writeBytes("new".encodeToByteArray())
            }
        }

        assertEquals("new", request.file.readBytes().decodeToString())
    }
}
