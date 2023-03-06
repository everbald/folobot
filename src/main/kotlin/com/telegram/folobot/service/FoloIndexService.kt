package com.telegram.folobot.service

import com.telegram.folobot.Utils
import com.telegram.folobot.extensions.getChatIdentity
import com.telegram.folobot.extensions.isAboutFo
import com.telegram.folobot.extensions.isFo
import com.telegram.folobot.model.NumTypeEnum
import com.telegram.folobot.model.dto.FoloIndexDto
import com.telegram.folobot.model.dto.toEntity
import com.telegram.folobot.persistence.entity.FoloIndexId
import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloIndexRepo
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Service
class FoloIndexService(
    private val foloIndexRepo: FoloIndexRepo,
    private val userService: UserService,
    private val messageService: MessageService,
    private val foloIndexChartService: FoloIndexChartService
) : KLogging() {
    companion object {
        const val PATH = "/static/images/index/"
        private val indexUp = listOf(
            "index_up1.png",
            "index_up2.png",
            "index_up3.png",
            "index_up4.png",
            "index_up5.png"
        )
        private val indexDown = listOf(
            "index_down1.png",
            "index_down2.png",
            "index_down3.png"
        )
        private val indexNeutral = listOf(
            "index_neutral1.png",
            "index_neutral2.png",
            "index_neutral3.png"
        )
    }

    fun getById(chatId: Long, date: LocalDate): FoloIndexDto {
        return foloIndexRepo.findIndexById(FoloIndexId(chatId, date))?.toDto()
            ?: FoloIndexDto(chatId, date)
    }

    fun addActivityPoints(update: Update) {
        if (update.hasMessage()) {
            val points =
                if (update.message.from.isFo()) 3
                else if (update.message.isAboutFo()) 2
                else 1
            foloIndexRepo.save(getById(update.message.chatId, LocalDate.now()).addPoints(points).toEntity())
            logger.trace {
                "Added $points activity points to chat ${getChatIdentity(update.message.chatId)} " +
                        "thanks to ${userService.getFoloUserName(update.message.from)}"
            }
        }
    }

    fun getAveragePoints(chatId: Long, date: LocalDate): Double {
        return foloIndexRepo.getAveragePointsByIdChatId(chatId, date.minusYears(1), date) ?: 0.0
    }

    fun calcAndSaveIndex(chatId: Long, date: LocalDate): Double {
        val foloIndex = getById(chatId, date)
        val average = getAveragePoints(chatId, date)
        foloIndex.index = if (average > 0) foloIndex.points / average * 100 else 0.0
        foloIndexRepo.save(foloIndex.toEntity())
        return foloIndex.index!!
    }

    fun dailyIndex(chatId: Long) {
        val photoPath: String
        val indexText: String

        val todayIndex = (calcAndSaveIndex(chatId, LocalDate.now()) * 100)
            .roundToInt().toDouble() / 100
        val yesterdayIndex = ((getById(chatId, LocalDate.now().minusDays(1)).index ?: 0.0) * 100)
            .roundToInt().toDouble() / 100
        val indexChange = ((todayIndex - yesterdayIndex) * 100).roundToInt()

        if (indexChange > 0) {
            photoPath = PATH + indexUp.random()
            indexText = "растет на ${Utils.getNumText(indexChange.absoluteValue, NumTypeEnum.POINT)}"
        } else if (indexChange < 0) {
            photoPath = PATH + indexDown.random()
            indexText = "падает на ${Utils.getNumText(indexChange.absoluteValue, NumTypeEnum.POINT)}"
        } else {
            photoPath = PATH + indexNeutral.random()
            indexText = "не изменился"
        }

        messageService.sendPhoto(
            photoPath, chatId,
            "Индекс фолоактивности *$indexText* и на сегодня составляет *$todayIndex%* от среднегодового значения\n" +
                    "#фолоиндекс"
        ).also { logger.info { "Sent foloindex to ${getChatIdentity(chatId)}" } }
    }

    fun monthlyIndex(chatId: Long) {
        val chart = foloIndexChartService.buildChart(
            chatId,
            LocalDate.now().minusMonths(1),
            LocalDate.now()
        )
        messageService.sendPhoto(chart, chatId,"#динамикафолоиндекса")
    }
}