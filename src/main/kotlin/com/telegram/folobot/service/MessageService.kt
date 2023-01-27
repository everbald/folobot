package com.telegram.folobot.service

import com.telegram.folobot.IdUtils.Companion.MESSAGE_QUEUE_ID
import com.telegram.folobot.IdUtils.Companion.getChatIdentity
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ActionType
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
    private val userService: UserService
) : KLogging() {
    lateinit var foloBot: FoloBot

    /**
     * Собрать объект [SendMessage]
     *
     * @param text   Текст сообщения
     * @param update [Update]
     * @return [SendMessage]
     */
    fun buildMessage(text: String, update: Update): SendMessage {
        return SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWN)
            .chatId(update.message.chatId.toString())
            .text(text)
            .build()
    }

    /**
     * Собрать объект [SendMessage]
     *
     * @param text   Текст сообщения
     * @param update [Update]
     * @param reply  В ответ на сообщение
     * @return [SendMessage]
     */
    fun buildMessage(text: String, update: Update, reply: Boolean): SendMessage {
        val sendMessage: SendMessage = buildMessage(text, update)
        if (reply) sendMessage.replyToMessageId = update.message.messageId
        return sendMessage
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param chatId ID чата(пользователя)
     */
    fun sendMessage(text: String, chatId: Long) {
        try {
            foloBot.execute(
                SendMessage
                    .builder()
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(chatId.toString())
                    .text(text)
                    .build()
            )
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param update [Update]
     * @return [Message]
     */
    fun sendMessage(text: String, update: Update): Message? {
        try {
            return foloBot.execute(buildMessage(text, update))
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
        return null
    }

    /**
     * Отправить сообщение буз реплая
     *
     * @param text   текст сообщения
     * @param update [Update]
     * @param reply  да/нет
     * @return [Message]
     */
    fun sendMessage(text: String, update: Update, reply: Boolean): Message? {
        if (!reply) {
            return sendMessage(text, update)
        } else {
            try {
                return foloBot.execute(buildMessage(text, update, reply))
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

    /**
     * Отправить стикер
     *
     * @param stickerId [String]
     * @param update      [Update]
     */
    fun sendSticker(stickerId: String?, update: Update) {
        stickerId?.let {
            try {
                foloBot.execute(buildSticker(it, update))
            } catch (e: TelegramApiException) {
                logger.error { e }
            }
        }
    }

    /**
     * Пересылка сообщений
     *
     * @param chatId Чат куда будет переслано сообщение
     * @param update [Update]
     */
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

    fun forwardMessage(chatId: Long, message: Message?) {
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
            } catch (e: TelegramApiException) {
                logger.error { e }
            }
        }
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

    /**
     * Отправить статус "печатает"
     *
     * @param update [Update]
     */
    fun sendChatTyping(update: Update) {
        try {
            foloBot.execute(
                SendChatAction
                    .builder()
                    .chatId(update.message.chatId.toString())
                    .action(ActionType.TYPING.toString())
                    .build()
            )
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    /**
     * Отправить изображение
     *
     * @param photoId идентификатор изображения
     * @param text    текст сообщения
     * @param chatId  ID чата(пользователя)
     */
    fun sendPhoto(photoId: String, text: String, chatId: Long) {
        try {
            foloBot.execute(
                SendPhoto
                    .builder()
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(chatId.toString())
                    .photo(InputFile(photoId))
                    .caption(text)
                    .build()
            )
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    /**
     * Отправить изображение
     *
     * @param photoPath путь к фото в ресурсах
     * @param text      текст сообщения
     * @param chatId    ID чата(пользователя)
     */
    fun sendPhotoFromResources(photoPath: String, text: String, chatId: Long) {
        try {
            foloBot.execute(
                SendPhoto
                    .builder()
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(chatId.toString())
                    .photo(
                        InputFile(
                            this::class.java.getResourceAsStream(photoPath),
                            photoPath.substringAfterLast("/")
                        )
                    )
                    .caption(text)
                    .build()
            )
        } catch (e: Exception) {
            logger.error { e }
        }
    }

    /**
     * Отправить аудио
     *
     * @param voiceId идентификатор audio
     * @param text    текст сообщения
     * @param chatId  ID чата(пользователя)
     */
    fun sendVoice(voiceId: String, text: String? = null, chatId: Long) {
        val voice = SendVoice
            .builder()
            .parseMode(ParseMode.MARKDOWN)
            .chatId(chatId.toString())
            .voice(InputFile(voiceId))
        text?.let { voice.caption(text) }
        try {
            foloBot.execute(voice.build())
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }


    /**
     * Удалить сообщение
     *
     * @param update [Update]
     */
    fun deleteMessage(update: Update) {
        try {
            foloBot.execute(DeleteMessage(update.message.chatId.toString(), update.message.messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    /**
     * Удалить сообщение
     */
    fun deleteMessage(chatId: Long, messageId: Int) {
        try {
            foloBot.execute(DeleteMessage(chatId.toString(), messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
        }
    }

    /**
     * Заместить сообщение
     *
     * @param update [Update]
     */
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
            logger.info { "found deleted message from ${userService.getFoloUserName(message.from)} " +
                    "in chat ${getChatIdentity(message.chatId)}" }
            true
        }
    }

    /**
     * Получить случайный стикер (из гиф пака)
     *
     * @return [InputFile]
     */
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