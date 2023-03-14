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
            message.isFolopidor() -> commandHandler.foloPidor(update)
            message.isFolopidorTop() -> commandHandler.foloPidorTop(update)
            message.isFoloMillionaire() -> commandHandler.foloMillionaire(update)
            message.isFoloIndexDinamics() -> commandHandler.foloIndexDinamics(update)
            else -> null
        }
    }

    fun isChatCommand(message: Message) =
        message.isSmallTalk() || message.isFolopidor() || message.isFolopidorTop() || message.isFoloMillionaire() ||
                message.isFoloIndexDinamics()


    private fun Message.isSmallTalk() = this.isAboutBot() &&
            this.text?.contains("адекватно", true) == true &&
            this.text?.contains("общ", true) == true

    private fun Message.isFolopidor() = this.isAboutBot() &&
            this.text?.contains("фолопидор", true) == true &&
            (this.text?.contains("дня", true) == true ||
                    this.text?.contains("сегодня", true) == true)

    private fun Message.isFolopidorTop() = this.isAboutBot() &&
            this.text?.contains("фолопидор", true) == true &&
            this.text?.contains("топ", true) == true

    private fun Message.isFoloMillionaire() = this.isAboutBot() &&
            this.text?.contains("фоломиллионер", true) == true ||
            (this.text?.contains("богатый", true) == true &&
                    this.text?.contains("фолопидор", true) == true)

    private fun Message.isFoloIndexDinamics() = this.isAboutBot() &&
            this.text?.contains("фолоиндекс", true) == true &&
            this.text?.contains("динамик", true) == true
}