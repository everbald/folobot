package com.everbald.folobot.config.telegram

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

@Configuration
class TelegramApplication {
    @Bean
    fun getTelegramApplication(): TelegramBotsLongPollingApplication = TelegramBotsLongPollingApplication()
}