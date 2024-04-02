package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.addActionReceived
import com.everbald.folobot.domain.type.Action
import com.everbald.folobot.service.SmallTalkService
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(3)
class TranscriptionHandler(
    private val smallTalkService: SmallTalkService
) : AbstractMessageHandler() {
    fun Message.isTranscribe() = this.hasVoice() || this.hasVideoNote()

    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isTranscribe())
            .also { if (it) logger.addActionReceived(Action.TRANSCRIPTION, update.message.chatId) }

    override fun handle(update: Update) = smallTalkService.transcription(update)
}