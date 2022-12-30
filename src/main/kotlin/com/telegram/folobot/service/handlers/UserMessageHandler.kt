package com.telegram.folobot.service.handlers

import com.telegram.folobot.IdUtils
import com.telegram.folobot.IdUtils.Companion.isAndrew
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
) : KLogging() {
    /**
     * Ответ на личное сообщение
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handle(update: Update): BotApiMethod<*>? {
        if (isAndrew(update.message.from) &&
            Random(System.nanoTime()).nextInt(100) < 7
        ) {
            messageService.forwardMessage(
                IdUtils.POC_ID,
                messageService.sendMessage(textService.quoteForAndrew, update, true)
                    .also { logger.info { "Replied to Andrew with ${it?.text}" } }
            )
        }
        return null
    }
}