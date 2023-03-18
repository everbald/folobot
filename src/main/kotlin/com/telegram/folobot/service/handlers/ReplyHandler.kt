package com.telegram.folobot.service.handlers

import com.telegram.folobot.extensions.addToLog
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
) : Handler, KLogging() {
    /**
     * Ответ на обращение
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    override fun handle(update: Update) {
        // Сообщение в чат
        if (update.message.from.isAndrew()) {
            messageService
                .sendMessage("Привет, моя сладкая бориспольская булочка!", update, reply = true)
        } else {
            messageService
                .sendMessage(
                    "Привет, уважаемый ${update.message.from.getPremiumPrefix()}" +
                            "фолофил ${userService.getFoloUserName(update.message.from)}!", update, reply = true
                )
        }.also { logger.addToLog(it) }
    }


}