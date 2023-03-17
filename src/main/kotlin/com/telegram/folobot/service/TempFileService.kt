package com.telegram.folobot.service

import mu.KLogging
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.file.FileSystemException
import java.nio.file.Files
import java.nio.file.Path

@Component
class TempFileService() : KLogging() {
    fun deleteTempFiles() {
        deletePathList().forEach {
            try {
                Files.deleteIfExists(Path.of(it))
                logger.info { "Deleted temp file $it" }
            } catch (e: FileSystemException) {
                logger.debug(e.message)
            } catch (e: IOException) {
                logger.error(e.message)
            }
        }
    }

    private fun deletePathList(): List<String> =
        File(System.getProperty("java.io.tmpdir"))
            .listFiles()
            ?.filter { it.isFile && it.isDeletionNeeded() }
            ?.map { it.absolutePath }
            ?: emptyList()

    private fun File.isDeletionNeeded() =
        this.name.contains("source") || this.name.contains("target")
}