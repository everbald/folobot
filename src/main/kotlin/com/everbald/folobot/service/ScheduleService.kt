package com.everbald.folobot.service

import com.everbald.folobot.utils.FoloId.FOLO_CHAT_ID
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleService(
    private val taskService: TaskService,
) {
    @Scheduled(cron = "0 59 23 ? * *")
    private fun dayStats() {
        taskService.dayStats(FOLO_CHAT_ID)
    }

    @Scheduled(cron = "0 * * ? * *")
    private fun foloCoin() = taskService.foloCoin()

    fun restoreMessages() = taskService.restoreMessages()

    @Scheduled(cron = "0 0 * ? * *")
    private fun deleteOutdatedMessages() = taskService.deleteOutdatedMessages()
}