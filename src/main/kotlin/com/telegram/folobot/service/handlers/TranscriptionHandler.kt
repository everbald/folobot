package com.telegram.folobot.service.handlers

import com.telegram.folobot.extensions.addActionReceived
import com.telegram.folobot.model.ActionsEnum
import com.telegram.folobot.service.OpenAIService
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(3)
class TranscriptionHandler(
    private val openAIService: OpenAIService
) : Handler, KLogging() {
    fun Message.isTranscribe() = this.hasVoice() || this.hasVideoNote()

    override fun canHandle(update: Update): Boolean {
        return update.message.isTranscribe().also {
            if (it) logger.addActionReceived(ActionsEnum.TRANSCRIPTION, update.message.chatId)
        }
    }

    override fun handle(update: Update) = openAIService.transcription(update)
}