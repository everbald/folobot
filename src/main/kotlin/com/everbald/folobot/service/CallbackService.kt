package com.everbald.folobot.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

@Service
class CallbackService(
    private val telegramClient: TelegramClient
) : KLogging() {
    fun answerCallbackQuery(update: Update) {
        try {
            telegramClient.execute(
                AnswerCallbackQuery.builder()
                    .callbackQueryId(update.callbackQuery.id)
                    .build()
            )
        } catch (ex: TelegramApiException) {
            logger.error { ex }
        }
    }

}