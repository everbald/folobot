package com.everbald.folobot


import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.support.DatabaseStartupValidator

@SpringBootApplication
@ImportAutoConfiguration(ExposedAutoConfiguration::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(DatabaseStartupValidator::class.java.name, *args)

}

