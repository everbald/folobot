package com.everbald.folobot.config.bot

import com.everbald.folobot.domain.type.UpdateField
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.DefaultBotOptions

@Configuration
class BotOptionsConfig {
    @Bean
    fun botOptions(): DefaultBotOptions =
        DefaultBotOptions()
            .apply { allowedUpdates = UpdateField.entries.map { it.fieldName } }
}