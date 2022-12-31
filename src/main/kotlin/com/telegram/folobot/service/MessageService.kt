package com.telegram.folobot.service

import com.telegram.folobot.Utils.printExeptionMessage
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
class MessageService : KLogging() {
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
            printExeptionMessage(e)
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
            printExeptionMessage(e)
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
                printExeptionMessage(e)
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
                printExeptionMessage(e)
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
            printExeptionMessage(e)
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
                printExeptionMessage(e)
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
            printExeptionMessage(e)
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
            printExeptionMessage(e)
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
            printExeptionMessage(e)
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
            printExeptionMessage(e)
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

    /**
     * Получить случайный войс
     *
     * @return [InputFile]
     */
    val randomVoice: String
        get() {
            return arrayOf(
                "AwACAgIAAx0CalJ4RAACBBVjsMmKi5mXdrptLdPr3l0iP1GgDwACPSAAAtdTaEmR_F2nU3-2SC0E",
                "AwACAgIAAx0CalJ4RAACBBZjsMmKzahNVLCHGsXr4rZI-kpr-AACQCAAAtdTaElCaBn1tNd33y0E",
                "AwACAgIAAx0CalJ4RAACBBdjsMmKJvi0kY6ZrQXcin5FjBZtTgACQSAAAtdTaEmfcn7TGEbnRS0E",
                "AwACAgIAAx0CalJ4RAACBBhjsMmKNJBkzTy9cDMcaAZsYhMVhwACRCAAAtdTaEmwShiRBaMuVS0E",
                "AwACAgIAAx0CalJ4RAACBBljsMmKfSlxH8HSoXn30vKeSZdZgAACRyAAAtdTaEnOjaACAklQ3S0E",
                "AwACAgIAAx0CalJ4RAACBBpjsMmKb-MRDMA84bs-QvS5-FQ83gACSSAAAtdTaEk15jd6A2irzi0E",
                "AwACAgIAAx0CalJ4RAACBBtjsMmKs3OkxcXJ4BGWy3rFIoU5ogACTyAAAtdTaElGaFICyutkdS0E",
                "AwACAgIAAx0CalJ4RAACBBxjsMmKjEZ1-nsrObYFz3E6OalKVAACUCAAAtdTaEmftSLquEa8GS0E",
                "AwACAgIAAx0CalJ4RAACBB1jsMmKlwPveslO3qG6hJGOWmpo2gACUSAAAtdTaEl8D6NwlexODy0E",
                "AwACAgIAAx0CalJ4RAACBB5jsMmKaVJ6KOLjI_todVKQ-9zyzgACViAAAtdTaEklG8wzFEN2Wi0E",
                "AwACAgIAAx0CalJ4RAACBB9jsMmK-rb94OCqEXrr8PHrAuKGWwACWiAAAtdTaEmXrdHjHqpcuS0E",
                "AwACAgIAAx0CalJ4RAACBCBjsMmKsIp1aANpMdd7qAyG9iU-FwACjSAAAtdTaEnvNbYGU1yo1C0E",
                "AwACAgIAAx0CalJ4RAACBCFjsMmK5VKXbBpYegU8KXObugLz8gACVCUAAjifcEkyH_coYlbNJS0E"
            ).random().also { logger.info { "Random voiceId is $it" } }

        }
}