package com.everbald.folobot.config.telegram

import com.everbald.folobot.config.JacksonConfig
import com.everbald.folobot.config.objectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import java.util.function.Supplier

@Configuration
class TelegramApplication {
    @Bean
    fun getTelegramApplication(): TelegramBotsLongPollingApplication =
        TelegramBotsLongPollingApplication {
            ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
}