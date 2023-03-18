package com.telegram.folobot.service.handlers

import com.telegram.folobot.extensions.addActionReceived
import com.telegram.folobot.extensions.isAndrew
import com.telegram.folobot.model.ActionsEnum
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.TextService
import com.telegram.folobot.utils.FoloId.POC_ID
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.random.Random

@Component
@Priority(7)
class UserMessageHandler(
    private val messageService: MessageService,
    private val textService: TextService
) : Handler, KLogging() {
    override fun canHandle(update: Update): Boolean {
        return (update.message.from.isAndrew() &&
                Random(System.nanoTime()).nextInt(100) < 7).also {
            if (it) logger.addActionReceived(ActionsEnum.USERMESSAGE, update.message.chatId)
        }
    }

    override fun handle(update: Update) {
        messageService.forwardMessage(
            POC_ID,
            messageService.sendMessage(textService.quoteForAndrew, update, reply = true)
                .also { logger.info { "Replied to Andrew with ${it?.text}" } }
        )
    }
}