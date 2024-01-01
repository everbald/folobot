package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.*
import com.everbald.folobot.domain.type.Action
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(5)
class ReplyHandler(
    private val userService: UserService,
    private val messageService: MessageService
) : AbstractMessageHandler() {
    fun Message.isGreetMe() = this.isNotForward && this.isAboutBot &&
            this.text?.contains("привет", ignoreCase = true) == true

    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isGreetMe())
            .also { if (it) logger.addActionReceived(Action.REPLY, update.message.chatId) }

    override fun handle(update: Update) {
        if (update.message.from.isAndrew) {
            messageService
                .sendMessage("Привет, моя сладкая бориспольская булочка!", update, reply = true)
        } else {
            messageService
                .sendMessage(
                    "Привет, уважаемый ${userService.getCustomName(update.from, update.chatId)}!",
                    update,
                    reply = true
                )
        }.also { logger.addMessage(it) }
    }
}