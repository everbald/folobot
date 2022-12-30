package com.telegram.folobot.service

import com.telegram.folobot.IdUtils
import com.telegram.folobot.Utils
import com.telegram.folobot.model.NumTypeEnum
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Service
class TaskService(
    private val messageService: MessageService,
    private val foloPidorService: FoloPidorService,
    private val userService: UserService,
    private val foloIndexService: FoloIndexService
) : KLogging() {
    companion object {
        const val STOCKS_UP_FILE_ID = "AgACAgIAAx0CalJ4RAACA-JjrpoX4BJQ-3E8K_cwvJMK5OdvRwACQ74xG-7AeEnhBk-2_2G9fAEAAwIAA3MAAy0E"
        const val STOCKS_DOWN_FILE_ID = "AgACAgIAAx0CalJ4RAACA-Njrpphj7-mGoWd30gW-x2-3heiCwACRr4xG-7AeElUbtWKdhQl8AEAAwIAA3MAAy0E"
        const val STOCKS_NEUTRAL_FILE_ID = "AgACAgIAAx0CalJ4RAACA71jqYeaF8ggrrXLp2Gr7_q6oM-hQgAC98QxG9HfSUlvTsOJyxrXSwEAAwIAA3MAAywE"
    }

    fun foloAnimal(chatId: Long) {
        messageService.sendVoice(chatId = chatId, voiceId = messageService.randomVoice)
    }

    fun dayStats(chatId: Long) {
        messageService.sendMessage(
            foloPidorService.getTopActive(chatId).withIndex().joinToString(
                separator = "\n",
                prefix = "*Самые активные фолопидоры сегодня*:\n\n",
                transform = {
                    "\u2004*${it.index + 1}*.\u2004${
                        userService.getFoloUserName(it.value, chatId)
                    } — ${Utils.getNumText(it.value.messagesPerDay, NumTypeEnum.MESSAGE)}"
                }
            ),
            chatId
        ).also { logger.info { "Sent day stats to ${IdUtils.getChatIdentity(chatId)}" } }
    }

    fun foloIndex(chatId: Long) {
        val photoId: String
        val indexText: String
        val forecast: String

        val todayIndex = (foloIndexService.calcAndSaveIndex(chatId, LocalDate.now()) * 100)
            .roundToInt().toDouble() / 100
        val yesterdayIndex = ((foloIndexService.getById(chatId, LocalDate.now().minusDays(1)).index ?: 0.0) * 100)
            .roundToInt().toDouble() / 100
        val indexChange = ((todayIndex - yesterdayIndex) * 100).roundToInt()

        if (indexChange > 0) {
            photoId = STOCKS_UP_FILE_ID
            indexText = "растет на ${indexChange.absoluteValue} пунктов"
            forecast = "Держать"
        } else if (indexChange < 0) {
            photoId = STOCKS_DOWN_FILE_ID
            indexText = "падает на ${indexChange.absoluteValue} пунктов"
            forecast = "Продавать"
        } else {
            photoId = STOCKS_NEUTRAL_FILE_ID
            indexText = "не изменился"
            forecast = "Держать"
        }

        messageService.sendPhoto(
            photoId,
            "Индекс фолоактивности *$indexText* и на сегодня составляет *$todayIndex%*\n" +
                    "Консенсус-прогноз: *$forecast* (_Основано на мнении 3 аналитиков_)",
            IdUtils.FOLO_TEST_CHAT_ID
        ).also { logger.info { "Sent foloindex to ${IdUtils.getChatIdentity(chatId)}" } }
    }
}