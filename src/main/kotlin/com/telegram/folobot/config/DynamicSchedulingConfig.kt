package com.telegram.folobot.config

import com.telegram.folobot.service.ScheduleService
import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
@EnableScheduling
@EnableAsync
class DynamicSchedulingConfig(
    private val scheduleService: ScheduleService
) : SchedulingConfigurer, KLogging() {
    @Bean
    fun taskExecutor(): Executor {
        return Executors.newSingleThreadScheduledExecutor()
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor())
        taskRegistrar.addTriggerTask(
            { scheduleService.restoreMessages() },
            {
                val lastCompletion = it.lastCompletion()
                    .also {
                        logger.trace {
                            "Last task completion time is ${
                                it?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) } ?: "undefined"
                            }"
                        }
                    }
                generateNextExecutionTime(lastCompletion)
                    .also {
                        logger.trace {
                            "Next task execution time is ${LocalDateTime.ofInstant(it, ZoneId.systemDefault())}"
                        }
                    }
            }
        )
    }

    private fun generateNextExecutionTime(lastCompletionTime: Instant?): Instant {
        return (lastCompletionTime ?: Instant.now()).plusSeconds(60)
    }

}