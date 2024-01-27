package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.addActionReceived
import com.everbald.folobot.extensions.from
import com.everbald.folobot.extensions.name
import com.everbald.folobot.extensions.isAboutBot
import com.everbald.folobot.domain.type.Action
import com.everbald.folobot.domain.type.BotCommand
import com.everbald.folobot.service.CommandService
import com.everbald.folobot.service.MessageService
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove

@Component
@Priority(2)
class ChatCommandHandler(
    private val smallTalkHandler: SmallTalkHandler,
    private val commandService: CommandService,
    private val messageService: MessageService
) : AbstractMessageHandler() {
    fun Message.isChatCommand() = !this.isReply &&
            (this.isSmallTalk() ||  this.isNoFap() || this.isFolopidor() ||
                    this.isCoin() || this.isTransferCancel())

    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isChatCommand())
        .also { if (it) logger.addActionReceived(Action.CHATCOMMAND, update.message.chatId) }

    override fun handle(update: Update) {
        val message = update.message
        when {
            message.isSmallTalk() -> smallTalkHandler.handle(update, true)
            message.isNoFap() -> commandService.nofapTimer(update)
            message.isFolopidor() -> commandService.foloPidorTop(update)
            message.isCoin() -> commandService.foloCoin(update)
            message.isTransferCancel() -> transferCancel(update)
            else -> {}
        }
    }

    private fun transferCancel(update: Update) = messageService.sendMessage(
        "Перевод фолокойна отменен",
        update,
        ReplyKeyboardRemove.builder().removeKeyboard(true).build()
    ).also { logger.info { "Folocoin transfer canceled by user ${update.from.name}" } }

    private fun Message.isSmallTalk() = this.isAboutBot &&
            (this.text?.contains("адекватно", true) == true &&
                    this.text?.contains("общ", true) == true)

    private fun Message.isFolopidor() = this.isAboutBot &&
            (this.text?.contains("фолопидор", true) == true &&
                    (this.text?.contains("дня", true) == true ||
                            this.text?.contains("сегодня", true) == true ||
                            this.text?.contains("топ", true) == true)
                    )

    private fun Message.isNoFap() = this.isAboutBot &&
            (this.text?.contains("но фап", true) == true ||
                    this.text?.contains("дрочишь", true) == true)

    private fun Message.isCoin() = this.isAboutBot &&
            this.text?.contains("фолобирж", true) == true

    private fun Message.isTransferCancel() =
        this.isUserMessage && this.text == BotCommand.FOLOCOINTRANSFERCANCEL.command
}