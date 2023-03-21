package com.everbald.folobot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class BotCredentialsConfig {
    @Value("\${bot.username}")
    val botUsername: String = ""

    @Value("\${bot.token}")
    val botToken: String = ""
}