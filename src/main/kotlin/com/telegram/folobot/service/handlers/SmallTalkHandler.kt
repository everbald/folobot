package com.telegram.folobot.service.handlers

import com.telegram.folobot.service.OpenAIService
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class SmallTalkHandler(
    private val openAIService: OpenAIService
) : KLogging() {
    /**
     * Ответ на обращение
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handle(update: Update, withSetup: Boolean = true): BotApiMethod<*>? {
        openAIService.smallTalk(update, withSetup)
        return null
    }
}