package com.telegram.folobot.config

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.minutes

@Configuration
class OpenAIClientConfig() {
    @Value("\${openai.token}")
    private val token: String = ""
    @Bean
    fun openAI(): OpenAI {
        return OpenAI(
            OpenAIConfig(
                token = token,
                logLevel = LogLevel.None,
                logger = Logger.Default,
                timeout = Timeout(socket = 1.minutes))
        )
    }
}