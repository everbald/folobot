package com.everbald.folobot.config

import feign.Logger
import feign.Request
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableFeignClients(basePackages = ["com.everbald.folobot"])
class FeignConfig {
    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.BASIC
    }
    @Bean
    fun requestOptions(): Request.Options {
        return Request.Options(
            30000,
            TimeUnit.MILLISECONDS,
            60000,
            TimeUnit.MILLISECONDS,
            true
        )
    }
}