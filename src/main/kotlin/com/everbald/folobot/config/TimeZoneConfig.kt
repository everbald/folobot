package com.everbald.folobot.config

import mu.KLogging
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Configuration
class TimeZoneConfig() : KLogging() {
    init {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))
        logger.info { "Bot started ${LocalDate.now()} at ${LocalTime.now().withNano(0)} MSK" }
        logger.info { "Hello, folo!" }
    }
}