package com.telegram.folobot.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.File


@Component
class FileService() : KLogging() { //TODO add logs
    lateinit var foloBot: FoloBot

    fun downloadPhoto(update: Update): File? {
        val filePath = getFilePath(update)
        return downloadPhotoByFilePath(filePath)
    }

    fun getFilePath(update: Update): String? {
        val photo = getPhoto(update)
        return photo?.filePath ?: runCatching {
            photo?.fileId?.let { foloBot.execute(GetFile.builder().fileId(it).build()).filePath }
        }.getOrNull()
    }

    private fun getPhoto(update: Update) = update.message?.photo?.maxByOrNull { it.fileSize }

    private fun downloadPhotoByFilePath(filePath: String?) =
        filePath?.let { runCatching { foloBot.downloadFile(filePath) }.getOrNull() }
}