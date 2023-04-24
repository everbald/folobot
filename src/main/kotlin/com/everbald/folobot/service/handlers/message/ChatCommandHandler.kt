package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.*
import com.everbald.folobot.model.Action
import com.everbald.folobot.model.BotCommand
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
    private val commandHandler: CommandHandler,
    private val messageService: MessageService
) : AbstractMessageHandler() {

    fun Message.isChatCommand() = !this.isReply &&
            (this.isSmallTalk() || this.isFreelance() || this.isNoFap() || this.isFolopidor() ||
                    this.isCoin() || this.isTransterCancel())

    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isChatCommand())
        .also { if (it) logger.addActionReceived(Action.CHATCOMMAND, update.message.chatId) }

    override fun handle(update: Update) {
        val message = update.message
        when {
            message.isSmallTalk() -> smallTalkHandler.handle(update)
            message.isFreelance() -> commandHandler.freelanceTimer(update)
            message.isNoFap() -> commandHandler.nofapTimer(update)
            message.isFolopidor() -> commandHandler.foloPidor(update)
            message.isCoin() -> commandHandler.foloCoin(update)
            message.isTransterCancel() -> transferCancel(update)
            else -> {}
        }
    }

    private fun transferCancel(update: Update) = messageService.sendMessage(
        "Перевод фолокойна отменен",
        update,
        ReplyKeyboardRemove.builder().removeKeyboard(true).build()
    ).also { logger.info { "Folocoin transfer canceled by user ${update.from.getName()}" } }

    private fun Message.isSmallTalk() = this.isAboutBot() &&
            (this.text?.contains("адекватно", true) == true &&
                    this.text?.contains("общ", true) == true)

    private fun Message.isFreelance() = this.isAboutBot() &&
            ((this.text?.contains("завод", true) == true &&
                    this.text?.contains("увол", true) == true) ||
                    this.text?.contains("фриланс", true) == true ||
                    ((this.text?.contains("входишь", true) == true ||
                            this.text?.contains("вхождение", true) == true) &&
                            (this.text?.contains("IT", true) == true) ||
                            (this.text?.contains("айти", true) == true)))

    private fun Message.isFolopidor() = this.isAboutBot() &&
            (this.text?.contains("фолопидор", true) == true &&
                    (this.text?.contains("дня", true) == true ||
                            this.text?.contains("сегодня", true) == true))

    private fun Message.isNoFap() = this.isAboutBot() &&
            (this.text?.contains("но фап", true) == true ||
                    this.text?.contains("дрочишь", true) == true)

    private fun Message.isCoin() = this.isAboutBot() &&
            this.text?.contains("фолобирж", true) == true

    private fun Message.isTransterCancel() =
        this.isUserMessage && this.text == BotCommand.FOLOCOINTRANSFERCANCEL.command
}