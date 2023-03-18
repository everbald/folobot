package com.everbald.folobot.config

import com.everbald.folobot.FoloBot
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramBotsApiConfig(private val foloBot: FoloBot) {
    init { TelegramBotsApi(DefaultBotSession::class.java).registerBot(foloBot) }
}