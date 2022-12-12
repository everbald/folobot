package com.telegram.folobot.config

import mu.KLogging
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalTime
import java.util.TimeZone
import javax.annotation.PostConstruct

@Configuration
class Configuration() : KLogging() {
    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))
        logger.info { "Bot started ${LocalDate.now()} at ${LocalTime.now().withNano(0)} MSK" }
        logger.info { "Hello folo!" }
    }
}