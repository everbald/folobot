package com.everbald.folobot.service

import com.everbald.folobot.extensions.toTextWithNumber
import com.everbald.folobot.domain.type.PluralType
import com.everbald.folobot.extensions.chatIdentity
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.FoloIndexService
import mu.KLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.OffsetDateTime

@Service
class TaskService(
    private val messageService: MessageService,
    private val foloPidorService: FoloPidorService,
    private val userService: UserService,
    private val foloIndexService: FoloIndexService,
    private val foloCoinService: FoloCoinService,
    private val foloBailService: FoloBailService,
    private val messageQueueService: MessageQueueService
) : KLogging() {

    fun dayStats(chatId: Long) {
        messageService.sendMessage("*Фолостатистика ${LocalDate.now().toTextWithNumber()}:*", chatId)
        topActive(chatId)
        dayBails(chatId)
        foloIndex(chatId)
    }

    fun topActive(chatId: Long) {
        foloPidorService.getTopActive(chatId, 3).withIndex().joinToString(
            separator = "\n",
            prefix = "*Самые активные фолопидоры*:\n",
            transform = {
                "\u2004*${it.index + 1}*.\u2004${
                    userService.getFoloUserName(it.value, chatId)
                } — ${it.value.messagesPerDay.toTextWithNumber(PluralType.MESSAGE)}"
            }
        ).let {messageService.sendMessage(it, chatId) }
            .also { logger.info { "Sent day stats to ${chatId.chatIdentity}" } }
    }

    fun dayBails(chatId: Long) {
        foloBailService.buildTodayBailText(chatId, fullList = false)
            .let { messageService.sendMessage(it, chatId) }
    }

    fun foloIndex(chatId: Long) {
        foloIndexService.dailyIndex(chatId)
        if (LocalDate.now().dayOfWeek == DayOfWeek.SUNDAY) foloIndexService.monthlyIndex(chatId)
    }

    fun foloCoin() = foloCoinService.issueCoins()

    @Async
    fun restoreMessages() = messageQueueService.restoreMessages()

    fun deleteOutdatedMessages() = messageService.deleteBefore(OffsetDateTime.now().minusMonths(1))
}