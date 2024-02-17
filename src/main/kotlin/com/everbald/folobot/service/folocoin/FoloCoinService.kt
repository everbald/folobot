package com.everbald.folobot.service.folocoin

import com.everbald.folobot.domain.FoloCoin
import com.everbald.folobot.extensions.name
import com.everbald.folobot.extensions.isAboutFo
import com.everbald.folobot.extensions.isFolochat
import com.everbald.folobot.extensions.isFromFoloSwarm
import com.everbald.folobot.extensions.round
import com.everbald.folobot.persistence.repo.FoloCoinRepo
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
import com.everbald.folobot.utils.FoloId.FOLOMKIN_ID
import com.everbald.folobot.utils.FoloId.FOLO_CHAT_ID
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import java.time.LocalDate
import kotlin.math.pow

@Service
class FoloCoinService(
    private val foloCoinRepo: FoloCoinRepo,
    private val userService: UserService,
    private val foloIndexService: FoloIndexService,
    private val messageService: MessageService,
) : KLogging() {
    private val THRESHOLD_MULTIPLIER = 10
    private val COIN_MULTIPLIER = 3

    private val coinThreshold get() = ((foloCoinRepo.getSumCoins() ?: 0) + 1) * THRESHOLD_MULTIPLIER
    private val coinPrice get() = 100 + (foloCoinRepo.getSumCoins() ?: 0) * COIN_MULTIPLIER

    private val averageIndex
        get() =
            (foloIndexService.getAverageIndex(FOLO_CHAT_ID, LocalDate.now().minusDays(1)))
                .run { if (this != 0.0) this else 100.0 }

    fun getById(userId: Long): FoloCoin = foloCoinRepo.findCoinByUserId(userId) ?: FoloCoin(userId)

    fun getTop(): List<FoloCoin> = foloCoinRepo.findTop10ByOrderByCoinsDescPointsDesc()

    fun addCoinPoints(update: Update) {
        if (update.message.chat.isFolochat) {
            val points = if (update.message.isAboutFo) 3 else 1
            val receiver =
                if (update.message.isFromFoloSwarm || update.message.isAutomaticForward == true) FOLOMKIN_ID
                else update.message.from.id
            getById(receiver)
                .addPoints(points)
                .let { foloCoinRepo.save(it) }
            logger.trace { "Added $points folocoin points to ${userService.getFoloUserName(receiver)}" }
        }
    }

    private fun getValidForCoinIssue(threshold: Int): List<FoloCoin> {
        return foloCoinRepo.findByPointsGreaterThanEqual(threshold)
    }

    fun issueCoins() {
        val threshold = coinThreshold
        if (threshold <= 10.0.pow(5)) {
            logger.trace { "Current coin threshold is $threshold" }
            getValidForCoinIssue(threshold).forEach {
                it.calcCoins(threshold)
                foloCoinRepo.save(it)
                logger.info { "Issued folocoin to ${userService.getFoloUserName(it.userId)}" }
            }
        } else {
            logger.trace { "Threshold limit reached" }
        }
    }

    fun getPrice(): Double = maxOf((coinPrice / 100.0 * averageIndex).round(), 100.0)

    fun issueCoins(userId: Long, amount: Int) {
        getById(userId)
            .addCoins(amount)
            .let { foloCoinRepo.save(it) }
    }

    fun transferCoin(update: Update) {
        val coinBalance = getById(update.message.from.id).coins
        if (coinBalance > 0) {
            issueCoins(update.message.userShared.userId, 1)
            issueCoins(update.message.from.id, -1)
            val sourceName = update.message.from.name
            val targetName = userService.getFoloUserName(update.message.userShared.userId)
            logger.info { "Successfully transferred folocoin from $sourceName to $targetName" }
            messageService.sendMessage(
                "Фолокойн переведен фолопидору $targetName",
                update,
                ReplyKeyboardRemove.builder().removeKeyboard(true).build()
            )
            messageService.sendMessage(
                "На твой фолосчет поступил перевод фолокойна от фолопидора $sourceName",
                update.message.userShared.userId,
            )
        } else {
            messageService.sendMessage(
                "На твоем счете нет фолокойнов досупных для трансфера дугому фолопидору 🫥",
                update,
                ReplyKeyboardRemove.builder().removeKeyboard(true).build()
            )
        }
    }
}