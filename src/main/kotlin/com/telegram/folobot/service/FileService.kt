package com.telegram.folobot.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.InputStream


@Component
class FileService() : KLogging() {
    lateinit var foloBot: FoloBot

    fun downloadPhoto(update: Update): InputStream? {
        val filePath = getFilePath(update)
        return downloadPhotoAsStream(filePath)
    }

    fun downloadPhoto(filePath: String): InputStream? {
        return downloadPhotoAsStream(filePath)
    }

    fun getFilePath(update: Update): String? {
        val photo = getPhoto(update)
        return photo?.filePath ?: runCatching {
            photo?.fileId?.let { foloBot.execute(GetFile.builder().fileId(it).build()).filePath }
        }.getOrElse {
            logger.error { it }
            null
        }
    }

    private fun getPhoto(update: Update) = update.message?.photo?.maxByOrNull { it.fileSize }

    private fun downloadPhotoByFilePath(filePath: String?) =
        filePath?.let {
            runCatching { foloBot.downloadFile(filePath) }.getOrElse {
                logger.error { it }
                null
            }
        }

    private fun downloadPhotoAsStream(filePath: String?) =
        filePath?.let {
            runCatching { foloBot.downloadFileAsStream(filePath) }.getOrElse {
                logger.error { it }
                null
            }
        }

}