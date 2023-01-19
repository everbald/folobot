package com.telegram.folobot.service.handlers

import com.telegram.folobot.IdUtils.Companion.getChatIdentity
import com.telegram.folobot.IdUtils.Companion.getPremium
import com.telegram.folobot.IdUtils.Companion.isAndrew
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.UserService
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.random.Random

@Component
class ReplyHandler(
    private val userService: UserService,
    private val messageService: MessageService
) : KLogging() {
    /**
     * Ответ на обращение
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handle(update: Update): BotApiMethod<*>? {
        // Сообщение в чат
        val text = update.message.text.lowercase()
        if (text.contains("привет") || Random(System.nanoTime()).nextInt(100) < 20) {
            val userName =
            return if (isAndrew(update.message.from)) {
                messageService
                    .buildMessage("Привет, моя сладкая бориспольская булочка!", update, true)
            } else {
                messageService
                    .buildMessage("Привет, уважаемый ${getPremium(update.message.from)}" +
                            "фолофил ${userService.getFoloUserName(update.message.from)}!", update, true)
            }.also { logger.info { "Replied to ${getChatIdentity(it.chatId)} with ${it.text}" } }
        }
        return null
    }
}