package com.everbald.folobot.service

import com.everbald.folobot.FoloBot
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class CallbackService(
    private val foloBot: FoloBot
) : KLogging() {
    fun answerCallbackQuery(update: Update) {
        try {
            foloBot.execute(
                AnswerCallbackQuery.builder()
                    .callbackQueryId(update.callbackQuery.id)
                    .build()
            )
        } catch (ex: TelegramApiException) {
            logger.error { ex }
        }
    }

}