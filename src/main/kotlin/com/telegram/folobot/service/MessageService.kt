package com.telegram.folobot.service

import com.telegram.folobot.FoloId.MESSAGE_QUEUE_ID
import com.telegram.folobot.extensions.getChatIdentity
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ForwardMessage
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.*
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
class MessageService(
    private val userService: UserService,
) : KLogging() {
    lateinit var foloBot: FoloBot

    fun buildMessage(text: String, update: Update, parseMode: String = ParseMode.MARKDOWN): SendMessage {
        return SendMessage
            .builder()
            .parseMode(parseMode)
            .chatId(update.message.chatId.toString())
            .text(text)
            .build()
    }

    fun buildMessage(
        text: String,
        update: Update,
        parseMode: String = ParseMode.MARKDOWN,
        reply: Boolean
    ): SendMessage {
        val sendMessage: SendMessage = buildMessage(text, update, parseMode)
        if (reply) sendMessage.replyToMessageId = update.message.messageId
        return sendMessage
    }

    fun sendMessage(text: String, chatId: Long, parseMode: String = ParseMode.MARKDOWN) {
        try {
            foloBot.execute(
                SendMessage
                    .builder()
                    .parseMode(parseMode)
                    .chatId(chatId.toString())
                    .text(text)
                    .build()
            )
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    fun sendMessage(text: String, update: Update, parseMode: String = ParseMode.MARKDOWN): Message? {
        try {
            return foloBot.execute(buildMessage(text, update, parseMode))
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
        return null
    }

    fun sendMessage(text: String, update: Update, parseMode: String = ParseMode.MARKDOWN, reply: Boolean): Message? {
        if (!reply) {
            return sendMessage(text, update, parseMode)
        } else {
            try {
                return foloBot.execute(buildMessage(text, update, parseMode, reply))
            } catch (e: TelegramApiException) {
                logger.error { e }
            }
        }
        return null
    }

    private fun buildSticker(stickerId: String, update: Update): SendSticker? {
        return SendSticker
            .builder()
            .chatId(update.message.chatId.toString())
            .sticker(InputFile(stickerId))
            .replyToMessageId(update.message.messageId)
            .build()
    }

    fun sendSticker(stickerId: String?, update: Update) {
        stickerId?.let {
            try {
                foloBot.execute(buildSticker(it, update))
            } catch (e: TelegramApiException) {
                logger.error { e }
            }
        }
    }

    fun forwardMessage(chatId: Long, update: Update) {
        try {
            foloBot.execute(
                ForwardMessage
                    .builder()
                    .chatId((chatId).toString())
                    .messageId(update.message.messageId)
                    .fromChatId(update.message.chatId.toString())
                    .build()
            )
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    fun forwardMessage(chatId: Long, message: Message?): Boolean {
        message?.let {
            try {
                foloBot.execute(
                    ForwardMessage
                        .builder()
                        .chatId(chatId.toString())
                        .messageId(it.messageId)
                        .fromChatId(it.chatId.toString())
                        .build()
                )
                return true
            } catch (e: TelegramApiException) {
                logger.error { e }
                return false
            }
        }
        return true
    }

    fun silentForwardMessage(chatId: Long, message: Message?): Message? {
        return message?.let {
            try {
                foloBot.execute(
                    ForwardMessage
                        .builder()
                        .chatId(chatId.toString())
                        .messageId(it.messageId)
                        .fromChatId(it.chatId.toString())
                        .disableNotification(true)
                        .build()
                )
            } catch (e: TelegramApiException) {
                null
            }
        }
    }

    fun buildPhoto(photo: InputFile, chatId: Long, text: String, parseMode: String = ParseMode.MARKDOWN): SendPhoto {
        return SendPhoto
            .builder()
            .parseMode(parseMode)
            .chatId(chatId.toString())
            .photo(photo)
            .caption(text)
            .build()
    }

    fun buildPhoto(photoPath: String, chatId: Long, text: String, parseMode: String = ParseMode.MARKDOWN): SendPhoto {
        return buildPhoto(
            InputFile(
                this::class.java.getResourceAsStream(photoPath),
                photoPath.substringAfterLast("/")
            ),
            chatId,
            text,
            parseMode
        )
    }


    fun sendPhoto(photo: InputFile, chatId: Long, text: String, parseMode: String = ParseMode.MARKDOWN) {
        try {
            foloBot.execute(
                buildPhoto(photo, chatId, text, parseMode)
            )
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    fun sendPhoto(photoPath: String, chatId: Long, text: String, parseMode: String = ParseMode.MARKDOWN) {
        try {
            foloBot.execute(
                buildPhoto(photoPath, chatId, text, parseMode)
            )
        } catch (e: Exception) {
            logger.error { e }
        }
    }

    fun sendVoice(voiceId: String, text: String? = null, chatId: Long, parseMode: String = ParseMode.MARKDOWN) {
        val voice = SendVoice
            .builder()
            .parseMode(parseMode)
            .chatId(chatId.toString())
            .voice(InputFile(voiceId))
        text?.let { voice.caption(text) }
        try {
            foloBot.execute(voice.build())
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }


    fun deleteMessage(update: Update) {
        try {
            foloBot.execute(DeleteMessage(update.message.chatId.toString(), update.message.messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    fun deleteMessage(chatId: Long, messageId: Int) {
        try {
            foloBot.execute(DeleteMessage(chatId.toString(), messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    fun substituteMessage(update: Update) {
        forwardMessage(update.message.chatId, update)
        deleteMessage(update)
    }

    fun checkIfMessageDeleted(message: Message): Boolean {
        val checkMsg = silentForwardMessage(MESSAGE_QUEUE_ID, message)
        return if (checkMsg != null) {
            deleteMessage(MESSAGE_QUEUE_ID, checkMsg.messageId)
            false
        } else {
            logger.info {
                "Found deleted message from ${userService.getFoloUserName(message.from)} " +
                        "in chat ${getChatIdentity(message.chatId)}"
            }
            true
        }
    }

    val randomSticker: String
        get() {
            return arrayOf(
                "CAACAgIAAxkBAAICCGKCCI-Ff-uqMZ-y4e0YmQEAAXp_RQAClxQAAnmaGEtOsbVbM13tniQE",
                "CAACAgIAAxkBAAPpYn7LsjgOH0OSJFBGx6WoIIKr_vcAAmQZAAJgRSBL_cLL_Nrl4OskBA",
                "CAACAgIAAxkBAAICCWKCCLoO6Itf6HSKKGedTPzbyeioAAJQFAACey0pSznSfTz0daK-JAQ",
                "CAACAgIAAxkBAAICCmKCCN_lePGRwqFYK4cPGBD4k_lpAAJcGQACmGshS9K8iR0VSuDVJAQ"
            ).random()
        }
}