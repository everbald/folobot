package com.telegram.folobot.service.handlers

import com.telegram.folobot.extensions.getChatIdentity
import com.telegram.folobot.extensions.getPremiumPrefix
import com.telegram.folobot.extensions.isAndrew
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.UserService
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

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
        return if (update.message.from.isAndrew()) {
            messageService
                .buildMessage("Привет, моя сладкая бориспольская булочка!", update, true)
        } else {
            messageService
                .buildMessage(
                    "Привет, уважаемый ${update.message.from.getPremiumPrefix()}" +
                            "фолофил ${userService.getFoloUserName(update.message.from)}!", update, true
                )
        }.also { logger.info { "Replied to ${getChatIdentity(it.chatId)} with ${it.text}" } }
    }
}