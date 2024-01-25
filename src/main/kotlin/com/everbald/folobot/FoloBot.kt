package com.everbald.folobot

import com.everbald.folobot.config.bot.BotCredentialsConfig
import com.everbald.folobot.event.UpdateReceivedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
final class FoloBot(
    private val botCredentials: BotCredentialsConfig,
    private val botOptions: DefaultBotOptions,
    private val applicationEventPublisher: ApplicationEventPublisher
) : TelegramLongPollingBot(botOptions, botCredentials.botToken) {
    override fun getBotUsername() = botCredentials.botUsername
    override fun onUpdateReceived(update: Update) = applicationEventPublisher.publishEvent(UpdateReceivedEvent(update))
}