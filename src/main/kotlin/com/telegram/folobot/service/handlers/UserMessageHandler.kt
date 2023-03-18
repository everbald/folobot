package com.telegram.folobot.service.handlers

import com.telegram.folobot.FoloId.POC_ID
import com.telegram.folobot.extensions.isAndrew
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.TextService
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.random.Random

@Component
class UserMessageHandler(
    private val messageService: MessageService,
    private val textService: TextService
) : Handler, KLogging() {
    /**
     * Ответ на личное сообщение
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    override fun handle(update: Update) {
        if (update.message.from.isAndrew() &&
            Random(System.nanoTime()).nextInt(100) < 7
        ) {
            messageService.forwardMessage(
                POC_ID,
                messageService.sendMessage(textService.quoteForAndrew, update, reply = true)
                    .also { logger.info { "Replied to Andrew with ${it?.text}" } }
            )
        }
    }
}