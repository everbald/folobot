package com.everbald.folobot.config.telegram

import com.everbald.folobot.config.bot.BotCredentialsConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient

@Configuration
class TelegramClientConfig(
    private val botCredentialsConfig: BotCredentialsConfig
) {
    @Bean
    fun telegramClient() = OkHttpTelegramClient(botCredentialsConfig.botToken)
}