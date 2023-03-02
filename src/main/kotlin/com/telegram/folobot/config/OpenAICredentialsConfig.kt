package com.telegram.folobot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAICredentialsConfig {
    @Value("\${openai.token}")
    val token: String = ""
}