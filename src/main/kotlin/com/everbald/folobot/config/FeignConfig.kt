package com.everbald.folobot.config

import feign.Logger
import feign.Request
import feign.codec.ErrorDecoder
import org.springframework.cloud.openfeign.DefaultFeignLoggerFactory
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignLoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.openfeign.FeignLogbookLogger
import java.util.concurrent.TimeUnit

@Configuration
@EnableFeignClients(basePackages = ["com.everbald.folobot"])
class FeignConfig {
    @Bean
    fun logbookFeign(): FeignLogbookLogger {
        return FeignLogbookLogger(LogbookConfig().logbook())
    }

    @Bean
    protected fun feignLoggerFactory(): FeignLoggerFactory {
        return DefaultFeignLoggerFactory(logbookFeign())
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.BASIC
    }

//    @Bean
//    fun errorDecoder(): ErrorDecoder {
//        return FeignErrorDecoder()
//    }

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