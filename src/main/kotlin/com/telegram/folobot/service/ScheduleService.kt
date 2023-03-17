package com.telegram.folobot.service

import com.telegram.folobot.FoloId.FOLO_CHAT_ID
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleService(
    private val taskService: TaskService,
) {
    @Scheduled(cron = "0 59 23 ? * *")
    private fun dayStats() = taskService.dayStats(FOLO_CHAT_ID)


    @Scheduled(cron = "1 59 23 ? * *")
    private fun foloIndex() = taskService.foloIndex(FOLO_CHAT_ID)

    @Scheduled(cron = "0 * * ? * *")
    private fun foloCoin() = taskService.foloCoin()

    fun restoreMessages() = taskService.restoreMessages()

    @Scheduled(cron = "0 */10 * ? * *")
    private fun deleteTempFiles() = taskService.deleteTempFiles()
}