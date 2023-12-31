package com.everbald.folobot.service.folocoin

import com.everbald.folobot.domain.FoloIndex
import com.everbald.folobot.extensions.getChatIdentity
import com.everbald.folobot.extensions.isAboutFo
import com.everbald.folobot.extensions.isFo
import com.everbald.folobot.extensions.round
import com.everbald.folobot.persistence.repo.FoloIndexRepo
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
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
        const val FOLO_STOCK_IMAGE = "/static/images/foloStock.png"
    }

    fun getById(chatId: Long, date: LocalDate): FoloIndex = foloIndexRepo.find(chatId, date) ?: FoloIndex(chatId, date)

    fun addActivityPoints(update: Update) {
        if (update.hasMessage()) {
            val points =
                if (update.message.from.isFo()) 3
                else if (update.message.isAboutFo()) 2
                else 1
            foloIndexRepo.save(getById(update.message.chatId, LocalDate.now()).addPoints(points))
            logger.trace {
                "Added $points activity points to chat ${getChatIdentity(update.message.chatId)} " +
                        "thanks to ${userService.getFoloUserName(update.message.from)}"
            }
        }
    }

    fun getAveragePoints(chatId: Long, date: LocalDate): Double =
       foloIndexRepo.getAveragePointsByChatId(chatId, date.minusYears(1), date) ?: 0.0

    fun getAverageIndex(chatId: Long, date: LocalDate): Double =
        foloIndexRepo.getAverageIndexByChatId(chatId, date.minusMonths(1), date) ?: 0.0


    fun calcAndSaveIndex(chatId: Long, date: LocalDate): Double {
        val foloIndex = getById(chatId, date)
        val average = getAveragePoints(chatId, date)
        foloIndex.index = if (average > 0) foloIndex.points / average * 100 else 0.0
        foloIndexRepo.save(foloIndex)
        return foloIndex.index!!
    }

    fun dailyIndex(chatId: Long) {
        val photoPath: String
        val indexText: String

        val todayIndex = (calcAndSaveIndex(chatId, LocalDate.now()) * 100)
            .roundToInt().toDouble() / 100
        val yesterdayIndex = ((getById(chatId, LocalDate.now().minusDays(1)).index ?: 0.0) * 100)
            .roundToInt().toDouble() / 100
        val indexChange = (todayIndex - yesterdayIndex).round()

        if (indexChange > 0) {
            photoPath = PATH + indexUp.random()
            indexText = "растет"
        } else if (indexChange < 0) {
            photoPath = PATH + indexDown.random()
            indexText = "падает"
        } else {
            photoPath = PATH + indexNeutral.random()
            indexText = "не изменился"
        }

        messageService.sendPhoto(
            photoPath, chatId,
            "Индекс фолоактивности *$indexText на ${indexChange.absoluteValue}%* " +
                    "и на сегодня составляет *$todayIndex%* от среднегодового значения\n#фолоиндекс"
        ).also { logger.info { "Sent foloindex to ${getChatIdentity(chatId)}" } }
    }

    fun monthlyIndex(chatId: Long) {
        val chart = foloIndexChartService.buildChart(
            chatId,
            LocalDate.now().minusMonths(1),
            LocalDate.now()
        )
        messageService.sendPhoto(chart, chatId,"#фолоиндекс")
    }
}