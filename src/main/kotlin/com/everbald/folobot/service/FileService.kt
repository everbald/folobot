package com.everbald.folobot.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.InputStream


@Component
class FileService(
    private val telegramClient: TelegramClient
) : KLogging() {
    fun downloadFileAsStream(update: Update): InputStream? {
        val filePath = getFilePath(update)
        return downloadFileAsStream(filePath)
    }

    fun getFilePath(update: Update): String? {
        return try {
            when {
                update.message.hasPhoto() -> {
                    val photo = getPhoto(update)
                    photo?.filePath ?: photo?.fileId
                        ?.let { telegramClient.execute(GetFile.builder().fileId(it).build()).filePath }
                }
                update.message.hasVoice() -> {
                    update.message?.voice?.fileId
                        ?.let { telegramClient.execute(GetFile.builder().fileId(it).build()).filePath }
                }
                update.message.hasVideoNote() -> {
                    update.message?.videoNote?.fileId
                        ?.let {telegramClient.execute(GetFile.builder().fileId(it).build()).filePath }
                }
                else -> null
            }
        } catch (ex: TelegramApiException) {
            logger.error { ex }
            null
        }
    }

    private fun getPhoto(update: Update) = update.message?.photo?.maxByOrNull { it.fileSize }

    private fun downloadFileAsStream(filePath: String?) =
        filePath?.let {
            runCatching { telegramClient.downloadFileAsStream(filePath) }.getOrElse {
                logger.error { it }
                null
            }
        }

}