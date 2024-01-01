package com.everbald.folobot.service

import com.everbald.folobot.extensions.getChatIdentity
import com.everbald.folobot.extensions.toTextWithNumber
import com.everbald.folobot.domain.type.PluralType
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.FoloIndexService
import mu.KLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate

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
        messageService.sendMessage(
            foloPidorService.getTopActive(chatId).withIndex().joinToString(
                separator = "\n",
                prefix = "*Самые активные фолопидоры сегодня*:\n\n",
                transform = {
                    "\u2004*${it.index + 1}*.\u2004${
                        userService.getFoloUserName(it.value, chatId)
                    } — ${it.value.messagesPerDay.toTextWithNumber(PluralType.MESSAGE)}"
                }
            ),
            chatId
        ).also { logger.info { "Sent day stats to ${getChatIdentity(chatId)}" } }
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
}