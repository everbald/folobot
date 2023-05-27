package com.everbald.folobot.service

import com.everbald.folobot.FoloBot
import com.everbald.folobot.extensions.chatId
import com.everbald.folobot.extensions.getChatIdentity
import com.everbald.folobot.extensions.messageId
import com.everbald.folobot.utils.FoloId.MESSAGE_QUEUE_ID
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ForwardMessage
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.methods.send.SendVoice
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMedia
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.InputStream

@Component
class MessageService(
    private val userService: UserService,
    private val foloBot: FoloBot
) : KLogging() {
    private fun buildMessage(
        text: String,
        update: Update,
        replyMarkup: ReplyKeyboard? = null,
        reply: Boolean = false,
        parseMode: String = ParseMode.MARKDOWN
    ) = SendMessage.builder()
        .parseMode(parseMode)
        .chatId(update.chatId)
        .text(text)
        .also { if (reply) it.replyToMessageId(update.message.messageId) }
        .also { sendMessage -> replyMarkup?.let<ReplyKeyboard, Unit> { sendMessage.replyMarkup(it) } }
        .build()

    fun sendMessage(text: String, chatId: Long, parseMode: String = ParseMode.MARKDOWN): Message? {
        return try {
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
            logger.error { "Message text was: $text" }
            null
        }
    }

    fun sendMessage(
        text: String,
        update: Update,
        replyMarkup: ReplyKeyboard? = null,
        reply: Boolean = false,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? {
        return try {
            return foloBot.execute(buildMessage(text, update, replyMarkup, reply, parseMode))
        } catch (e: TelegramApiException) {
            logger.error { e }
            logger.error { "Message text was: $text" }
            null
        }
    }

    fun buildEditMessageText(
        text: String,
        update: Update,
        replyMarkup: InlineKeyboardMarkup,
        parseMode: String = ParseMode.MARKDOWN
    ): EditMessageText = EditMessageText
        .builder()
        .messageId(update.messageId)
        .chatId(update.chatId)
        .parseMode(parseMode)
        .text(text)
        .replyMarkup(replyMarkup)
        .build()

    fun editMessageText(
        text: String,
        update: Update,
        replyMarkup: InlineKeyboardMarkup,
        parseMode: String = ParseMode.MARKDOWN
    ) {
        try {
            foloBot.execute(buildEditMessageText(text, update, replyMarkup, parseMode))
        } catch (e: TelegramApiException) {
            logger.debug { e }
        }
    }

    fun buildEditMessageCaption(
        text: String,
        update: Update,
        replyMarkup: InlineKeyboardMarkup,
        parseMode: String = ParseMode.MARKDOWN
    ): EditMessageCaption = EditMessageCaption
        .builder()
        .messageId(update.messageId)
        .chatId(update.chatId)
        .parseMode(parseMode)
        .caption(text)
        .replyMarkup(replyMarkup)
        .build()

    fun editMessageCaption(
        text: String,
        update: Update,
        replyMarkup: InlineKeyboardMarkup,
        parseMode: String = ParseMode.MARKDOWN
    ) {
        try {
            foloBot.execute(buildEditMessageCaption(text, update, replyMarkup, parseMode))
        } catch (e: TelegramApiException) {
            logger.debug { e }
        }
    }

    fun buildInputMediaPhoto(photo: InputFile, text: String?, parseMode: String = ParseMode.MARKDOWN): InputMedia =
        InputMediaPhoto()
            .also { mediaPhoto ->
                mediaPhoto.setMedia(photo.newMediaStream, photo.mediaName)
                text?.let { mediaPhoto.caption = it }
                mediaPhoto.parseMode = parseMode
            }

    fun buildEditMessagePhoto(
        photo: InputFile,
        text: String?,
        update: Update,
        replyMarkup: InlineKeyboardMarkup,
        parseMode: String = ParseMode.MARKDOWN
    ): EditMessageMedia =
        EditMessageMedia
            .builder()
            .messageId(update.messageId)
            .chatId(update.chatId)
            .replyMarkup(replyMarkup)
            .media(buildInputMediaPhoto(photo, text))
            .build()

    fun editMessagePhoto(
        photo: InputFile,
        text: String?,
        update: Update,
        replyMarkup: InlineKeyboardMarkup,
        parseMode: String = ParseMode.MARKDOWN
    ) {
        try {
            foloBot.execute(buildEditMessagePhoto(photo, text, update, replyMarkup, parseMode))
        } catch (e: TelegramApiException) {
            logger.debug { e }
        }
    }

    private fun buildSticker(stickerId: String, update: Update): SendSticker? = SendSticker.builder()
        .chatId(update.message.chatId.toString())
        .sticker(InputFile(stickerId))
        .replyToMessageId(update.message.messageId)
        .build()

    fun sendSticker(stickerId: String, update: Update): Message? =
        try {
            foloBot.execute(buildSticker(stickerId, update))
        } catch (e: TelegramApiException) {
            logger.error { e }
            null
        }

    fun forwardMessage(chatId: Long, update: Update): Message? =
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
            null
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

    fun buildPhoto(
        photo: InputFile,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): SendPhoto = SendPhoto.builder()
        .parseMode(parseMode)
        .chatId(chatId.toString())
        .photo(photo)
        .also { sendPhoto -> text?.let { sendPhoto.caption(text) } }
        .also { sendPhoto -> replyMarkup?.let { sendPhoto.replyMarkup(replyMarkup) } }
        .build()

    fun buildPhoto(
        photoPath: String,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): SendPhoto {
        return buildPhoto(
            InputFile(
                this::class.java.getResourceAsStream(photoPath),
                photoPath.substringAfterLast("/")
            ),
            chatId,
            text,
            replyMarkup,
            parseMode
        )
    }

    fun sendPhoto(
        photo: InputFile,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? {
        return try {
            return foloBot.execute(buildPhoto(photo, chatId, text, replyMarkup, parseMode))
        } catch (e: TelegramApiException) {
            logger.error { e }
            null
        }
    }

    fun sendPhoto(
        photoPath: String,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? {
        return try {
            foloBot.execute(buildPhoto(photoPath, chatId, text, replyMarkup, parseMode))
        } catch (e: Exception) {
            logger.error { e }
            null
        }
    }

    private fun buildVoice(
        voiceId: String,
        text: String? = null,
        chatId: Long,
        parseMode: String = ParseMode.MARKDOWN
    ) = SendVoice.builder()
        .parseMode(parseMode)
        .chatId(chatId.toString())
        .voice(InputFile(voiceId))
        .also { voice -> text?.let { voice.caption(text) } }
        .build()

    fun sendVoice(
        voiceId: String,
        text: String? = null,
        chatId: Long,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? {
        return try {
            return foloBot.execute(buildVoice(voiceId, text, chatId, parseMode))
        } catch (e: TelegramApiException) {
            logger.error { e }
            null
        }
    }


    fun deleteMessage(update: Update): Boolean {
        return try {
            foloBot.execute(DeleteMessage(update.message.chatId.toString(), update.message.messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
            false
        }
    }

    fun deleteMessage(chatId: Long, messageId: Int): Boolean {
        return try {
            return foloBot.execute(DeleteMessage(chatId.toString(), messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
            false
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