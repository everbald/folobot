package com.telegram.folobot.service

import com.telegram.folobot.FoloBot
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.io.InputStream


@Component
class FileService(
    private var foloBot: FoloBot
) : KLogging() {


    fun downloadFileAsStream(update: Update): InputStream? {
        val filePath = getFilePath(update)
        return downloadFileAsStream(filePath)
    }

    fun downloadFile(update: Update, file: File) {
        val filePath = getFilePath(update)
        downloadFile(filePath, file)
    }

    fun getFilePath(update: Update): String? {
        return try {
            when {
                update.message.hasPhoto() -> {
                    val photo = getPhoto(update)
                    photo?.filePath ?: photo?.fileId
                        ?.let { foloBot.execute(GetFile.builder().fileId(it).build()).filePath }
                }
                update.message.hasVoice() -> {
                    update.message?.voice?.fileId
                        ?.let { foloBot.execute(GetFile.builder().fileId(it).build()).filePath }
                }
                update.message.hasVideoNote() -> {
                    update.message?.videoNote?.fileId
                        ?.let {foloBot.execute(GetFile.builder().fileId(it).build()).filePath }
                }
                else -> null
            }
        } catch (ex: TelegramApiException) {
            logger.error { ex }
            null
        }
    }

    private fun getPhoto(update: Update) = update.message?.photo?.maxByOrNull { it.fileSize }

    fun downloadFile(filePath: String?, file: File) =
        filePath?.let {
            runCatching { foloBot.downloadFile(filePath, file) }.getOrElse {
                logger.error { it }
                null
            }
        }

    private fun downloadFileAsStream(filePath: String?) =
        filePath?.let {
            runCatching { foloBot.downloadFileAsStream(filePath) }.getOrElse {
                logger.error { it }
                null
            }
        }

}