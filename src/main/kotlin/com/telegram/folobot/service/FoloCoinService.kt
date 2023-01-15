package com.telegram.folobot.service

import com.telegram.folobot.IdUtils.Companion.isAboutFo
import com.telegram.folobot.IdUtils.Companion.isFoloTestChat
import com.telegram.folobot.IdUtils.Companion.isFolochat
import com.telegram.folobot.model.dto.FoloCoinDto
import com.telegram.folobot.model.dto.FoloPidorDto
import com.telegram.folobot.model.dto.toEntity
import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloCoinRepo
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate

@Service
class FoloCoinService(
    private val foloCoinRepo: FoloCoinRepo,
    private val userService: UserService
) : KLogging() {
    fun getById(userId: Long): FoloCoinDto {
        return foloCoinRepo.findCoinByUserId(userId)?.toDto() ?: FoloCoinDto(userId)
    }

    fun getTop(): List<FoloCoinDto> {
        return foloCoinRepo.findTop10ByOrderByCoinsDesc().map { it.toDto() }
    }

    fun addCoinPoints(update: Update) {
        if (update.hasMessage() && isFolochat(update.message.chat)) {
            val points = if (isAboutFo(update)) 3 else 1
            foloCoinRepo.save(getById(update.message.from.id).addPoints(points).toEntity())
            logger.info { "Added $points folocoin points to ${userService.getFoloUserName(update.message.from)}" }
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
        logger.info { "Current coin threshold is $threshold" }
        getValidForCoinIssue(threshold).forEach {
            it.calcCoins(threshold)
            foloCoinRepo.save(it.toEntity())
            logger.info { "Issued folocoin to ${userService.getFoloUserName(it.userId)}"  }
        }
    }
}