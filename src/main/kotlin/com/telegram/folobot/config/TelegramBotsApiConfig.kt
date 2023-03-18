package com.telegram.folobot.config

import com.telegram.folobot.FoloBot
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramBotsApiConfig(private val foloBot: FoloBot) {
    init { TelegramBotsApi(DefaultBotSession::class.java).registerBot(foloBot) }
}