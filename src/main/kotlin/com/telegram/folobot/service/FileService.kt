package com.telegram.folobot.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.File
import java.io.InputStream


@Component
class FileService() : KLogging() {
    lateinit var foloBot: FoloBot

    fun downloadFileAsStream(update: Update): InputStream? {
        val filePath = getFilePath(update)
        return downloadFileAsStream(filePath)
    }

    fun downloadFile(update: Update): File? {
        val filePath = getFilePath(update)
        return downloadFile(filePath)
    }

    fun getFilePath(update: Update): String? {
        return when {
            update.message.hasPhoto() -> {
                val photo = getPhoto(update)
                photo?.filePath ?: runCatching {
                    photo?.fileId?.let { foloBot.execute(GetFile.builder().fileId(it).build()).filePath }
                }.getOrElse {
                    logger.error { it }
                    null
                }
            }
            update.message.hasVoice() -> {
                update.message?.voice?.fileId?.let {
                    runCatching {
                        foloBot.execute(GetFile.builder().fileId(it).build()).filePath
                    }
                }?.getOrElse {
                    logger.error { it }
                    null
                }
            }
            else -> null
        }
    }

    private fun getPhoto(update: Update) = update.message?.photo?.maxByOrNull { it.fileSize }

    fun downloadFile(filePath: String?) =
        filePath?.let {
            runCatching { foloBot.downloadFile(filePath) }.getOrElse {
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