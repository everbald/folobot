package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.config.bot.BotCredentialsConfig
import com.everbald.folobot.extensions.*
import com.everbald.folobot.domain.type.Action
import com.everbald.folobot.domain.type.BotCommand
import com.everbald.folobot.service.CommandService
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.TaskService
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(1)
class CommandHandler(
    private val commandService: CommandService,
    private val messageService: MessageService,
    private val smallTalkHandler: SmallTalkHandler,
    private val taskService: TaskService,
    private val botCredentials: BotCredentialsConfig
) : AbstractMessageHandler() {
    fun Message.isMyCommand() =
        this.isCommand && this.isNotForward &&
                (this.chat.isUserChat ||
                        this.entities.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text
                            ?.contains(botCredentials.botUsername) == true)

    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isMyCommand())
        .also { if (it) logger.addActionReceived(Action.COMMAND, update.message.chatId) }

    override fun handle(update: Update) {
        when (
            BotCommand.fromCommand(update.message.getBotCommand()).also {
                logger.addCommandReceived(
                    it,
                    update.message.chatId.chatIdentity,
                    update.from.name
                )
            }
        ) {
            BotCommand.START -> messageService.sendSticker(messageService.randomSticker, update)
                .also { logger.info { "Sent sticker to ${update.message.chatId.chatIdentity}" } }

            BotCommand.SILENTSTREAM -> messageService.sendSticker(messageService.randomSticker, update)
                .also { logger.info { "Sent sticker to ${update.message.chatId.chatIdentity}" } }

            BotCommand.SMALLTALK -> smallTalkHandler.handle(update, true)
            BotCommand.FREELANCE -> commandService.freelanceTimer(update)
            BotCommand.NOFAP -> commandService.nofapTimer(update)
            BotCommand.FOLOPIDOR -> commandService.foloPidor(update)
            BotCommand.FOLOPIDORALPHA -> commandService.alphaTimer(update)
            BotCommand.FOLOCOIN -> commandService.foloCoin(update)
            BotCommand.FOLOCOINTRANSFER -> commandService.foloCoinTransfer(update)
            BotCommand.IT -> commandService.aboutIt(update)
            BotCommand.FOLOBAIL -> commandService.foloBail(update)
            BotCommand.IMAGE -> commandService.createImage(update)
            BotCommand.DAYSTATS -> taskService.dayStats(update.chatId)
            else -> {}
        }
    }
}