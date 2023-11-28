package com.everbald.folobot.config

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import mu.KLogger
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.minutes

@Configuration
class OpenAIClientConfig() {
    @Value("\${openai.token}")
    private val token: String = ""
    private val logger: KLogger = KotlinLogging.logger { this::class.java }

    @Bean
    fun openAI(): OpenAI =
        OpenAIConfig(
            token = token,
            logging = LoggingConfig(LogLevel.None, Logger.Default),
            timeout = Timeout(socket = 1.minutes)
        ).let { config ->
            OpenAI(config)
                .also { logger.info { "OpenAI config: ${config.token}" } }
        }

}