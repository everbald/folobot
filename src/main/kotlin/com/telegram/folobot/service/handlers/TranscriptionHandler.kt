package com.telegram.folobot.service.handlers

import com.telegram.folobot.service.OpenAIService
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class TranscriptionHandler(
    private val openAIService: OpenAIService
) : Handler, KLogging() {
    override fun handle(update: Update): BotApiMethod<*>? {
        openAIService.transcription(update)
        return null
    }
}