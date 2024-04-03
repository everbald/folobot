package com.everbald.folobot.config.telegram

import com.everbald.folobot.config.objectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import java.util.function.Supplier

@Configuration
class TelegramApplication {
    @Bean
    fun getTelegramApplication(): TelegramBotsLongPollingApplication =
        TelegramBotsLongPollingApplication { objectMapper }
}