package com.everbald.folobot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.DatabaseStartupValidator
import javax.sql.DataSource

@Configuration
class DatabaseStartupValidatorConfig {
    @Bean
    fun databaseStartupValidator(dataSource: DataSource): DatabaseStartupValidator =
        DatabaseStartupValidator()
            .also { it.setDataSource(dataSource) }
}