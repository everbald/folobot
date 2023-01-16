package com.telegram.folobot.service

import com.telegram.folobot.IdUtils.Companion.FOLOMKIN_ID
import com.telegram.folobot.IdUtils.Companion.isAboutFo
import com.telegram.folobot.IdUtils.Companion.isFolochat
import com.telegram.folobot.IdUtils.Companion.isFromFoloSwarm
import com.telegram.folobot.model.dto.FoloCoinDto
import com.telegram.folobot.model.dto.toEntity
import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloCoinRepo
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.math.pow

@Service
class FoloCoinService(
    private val foloCoinRepo: FoloCoinRepo,
    private val userService: UserService
) : KLogging() {
    fun getById(userId: Long): FoloCoinDto {
        return foloCoinRepo.findCoinByUserId(userId)?.toDto() ?: FoloCoinDto(userId)
    }

    fun getTop(): List<FoloCoinDto> {
        return foloCoinRepo.findTop10ByOrderByCoinsDescPointsDesc().map { it.toDto() }
    }

    fun addCoinPoints(update: Update) {
        if (update.hasMessage() && isFolochat(update.message.chat)) {
            val points = if (isAboutFo(update)) 3 else 1
//            val receiver = if (isFromFoloSwarm(update) || update.message.isAutomaticForward == true) FOLOMKIN_ID
            val receiver = if (update.message.isAutomaticForward == true) FOLOMKIN_ID
            else update.message.from.id
            foloCoinRepo.save(getById(receiver).addPoints(points).toEntity())
            logger.info { "Added $points folocoin points to ${userService.getFoloUserName(receiver)}" }
        }
    }

    fun getCoinThreshold(): Int {
        return ((foloCoinRepo.getSumCoins() ?: 0) + 1) * 10
    }

    fun getValidForCoinIssue(threshold: Int): List<FoloCoinDto> {
        return foloCoinRepo.findByPointsGreaterThanEqual(threshold).map { it.toDto() }
    }

    fun issueCoins() {
        val threshold = getCoinThreshold()
        if (threshold <= 10.0.pow(5)) {
            logger.trace { "Current coin threshold is $threshold" }
            getValidForCoinIssue(threshold).forEach {
                it.calcCoins(threshold)
                foloCoinRepo.save(it.toEntity())
                logger.info { "Issued folocoin to ${userService.getFoloUserName(it.userId)}" }
            }
        } else {
            logger.trace { "Threshold limit reached" }
        }
    }
}