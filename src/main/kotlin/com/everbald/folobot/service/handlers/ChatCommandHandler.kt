package com.everbald.folobot.service.handlers

import com.everbald.folobot.extensions.addActionReceived
import com.everbald.folobot.extensions.isAboutBot
import com.everbald.folobot.model.Action
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(2)
class ChatCommandHandler(
    private val smallTalkHandler: SmallTalkHandler,
    private val commandHandler: CommandHandler,
) : Handler, KLogging() {

    fun Message.isChatCommand() = !this.isReply &&
            (this.isSmallTalk() || this.isFreelance() || this.isNoFap() || this.isFolopidor() ||
                    this.isFolopidorTop() || this.isCoin() || this.isFoloIndexDinamics())

    override fun canHandle(update: Update): Boolean {
        return (update.hasMessage() && update.message.isChatCommand()).also {
            if (it) logger.addActionReceived(Action.CHATCOMMAND, update.message.chatId)
        }
    }

    override fun handle(update: Update) {
        val message = update.message
        when {
            message.isSmallTalk() -> smallTalkHandler.handle(update)
            message.isFreelance() -> commandHandler.frelanceTimer(update)
            message.isNoFap() -> commandHandler.nofapTimer(update)
            message.isFolopidor() -> commandHandler.foloPidor(update)
            message.isFolopidorTop() -> commandHandler.foloPidorTop(update)
            message.isCoin() -> commandHandler.foloCoin(update)
            message.isFoloIndexDinamics() -> commandHandler.foloIndexDinamics(update)
            else -> {}
        }
    }

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

    private fun Message.isFolopidorTop() = this.isAboutBot() &&
            (this.text?.contains("фолопидор", true) == true &&
                    this.text?.contains("топ", true) == true)

    private fun Message.isCoin() = this.isAboutBot() &&
            this.text?.contains("фолобирж", true ) == true

    private fun Message.isFoloIndexDinamics() = this.isAboutBot() &&
            (this.text?.contains("фолоиндекс", true) == true &&
                    this.text?.contains("динамик", true) == true)
}