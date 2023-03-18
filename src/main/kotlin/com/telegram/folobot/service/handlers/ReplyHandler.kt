package com.telegram.folobot.service.handlers

import com.telegram.folobot.extensions.*
import com.telegram.folobot.model.ActionsEnum
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.UserService
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(5)
class ReplyHandler(
    private val userService: UserService,
    private val messageService: MessageService
) : Handler, KLogging() {
    fun Message.isGreetMe() = this.isNotForward() && this.isAboutBot() &&
            this.text?.contains("привет", ignoreCase = true) == true

    override fun canHandle(update: Update): Boolean {
        return update.message.isGreetMe().also {
            if (it) logger.addActionReceived(ActionsEnum.REPLY, update.message.chatId)
        }
    }

    override fun handle(update: Update) {
        if (update.message.from.isAndrew()) {
            messageService
                .sendMessage("Привет, моя сладкая бориспольская булочка!", update, reply = true)
        } else {
            messageService
                .sendMessage(
                    "Привет, уважаемый ${update.message.from.getPremiumPrefix()}" +
                            "фолофил ${userService.getFoloUserName(update.message.from)}!", update, reply = true
                )
        }.also { logger.addMessage(it) }
    }
}