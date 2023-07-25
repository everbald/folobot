package com.everbald.folobot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.Logbook
import org.zalando.logbook.core.Conditions.exclude
import org.zalando.logbook.core.Conditions.requestTo
import org.zalando.logbook.core.DefaultHttpLogFormatter
import org.zalando.logbook.core.DefaultHttpLogWriter
import org.zalando.logbook.core.DefaultSink

@Configuration
class LogbookConfig {
    @Bean
    fun logbook(): Logbook {
        return Logbook.builder()
            .condition(
                exclude(
                    requestTo("/actuator/**"),
                    requestTo("/swagger/**"),
                    requestTo("/swagger-ui/**"),
                    requestTo("/swagger-resources/**"),
                    requestTo("/v2/api-docs"),
                    requestTo("/v3/api-docs/**")
                )
            )
            .sink(DefaultSink(DefaultHttpLogFormatter(), DefaultHttpLogWriter()))
            .build()
    }
}
