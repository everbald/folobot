package com.telegram.folobot.service.handlers

import com.telegram.folobot.extensions.isAboutBot
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ChatCommandHandler(
    private val smallTalkHandler: SmallTalkHandler,
    private val commandHandler: CommandHandler,
) : Handler, KLogging() {
    override fun handle(update: Update): BotApiMethod<*>? {
        val message = update.message
        return when {
            message.isSmallTalk() -> smallTalkHandler.handle(update)
            message.isFreelance() -> commandHandler.frelanceTimer(update)
            message.isNoFap() -> commandHandler.nofapTimer(update)
            message.isFolopidor() -> commandHandler.foloPidor(update)
            message.isFolopidorTop() -> commandHandler.foloPidorTop(update)
            message.isCoinBalance() -> commandHandler.coinBalance(update)
            message.isFoloMillionaire() -> commandHandler.foloMillionaire(update)
            message.isFoloIndexDinamics() -> commandHandler.foloIndexDinamics(update)
            else -> null
        }
    }

    fun isChatCommand(message: Message) = !message.isReply &&
            (message.isSmallTalk() || message.isFreelance() || message.isNoFap() || message.isFolopidor() ||
                    message.isFolopidorTop() || message.isCoinBalance() || message.isFoloMillionaire() ||
                    message.isFoloIndexDinamics())

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

    private fun Message.isCoinBalance() = this.isAboutBot() &&
            ((this.text?.contains("баланс", true) == true &&
                    this.text?.contains("кошелька", true) == true) ||
            (this.text?.contains("сколько", true) == true &&
                    this.text?.contains("фолокойнов", true) == true) ||
            (this.text?.contains("баланс", true) == true &&
                    this.text?.contains("фолокойнов", true) == true))

    private fun Message.isFoloMillionaire() = this.isAboutBot() &&
            (this.text?.contains("фоломиллионер", true) == true ||
            (this.text?.contains("богатый", true) == true &&
                    this.text?.contains("фолопидор", true) == true))

    private fun Message.isFoloIndexDinamics() = this.isAboutBot() &&
            (this.text?.contains("фолоиндекс", true) == true &&
            this.text?.contains("динамик", true) == true)
}