package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.domain.FoloPidorWithCount
import com.everbald.folobot.persistence.repo.FoloPidorRepo
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class FoloPidorService(private val repo: FoloPidorRepo) {
    private val logger: KLogger = KotlinLogging.logger { this::class.java }

    fun findAll(): List<FoloPidor> =
        repo.getAll()
            .sortedWith(compareBy<FoloPidor> { it.chatId }.thenByDescending { it.score })

    fun find(chatId: Long, userId: Long): FoloPidor = repo.find(chatId, userId) ?: FoloPidor(chatId, userId)

    fun exists(chatId: Long, userId: Long): Boolean = repo.exists(chatId, userId)

    fun findByChatId(chatId: Long): List<FoloPidor> =
        repo.findByChatId(chatId)
            .sortedWith(compareBy<FoloPidor> { it.chatId }.thenByDescending { it.score })

    fun getTop(chatId: Long): List<FoloPidor> =
        repo.findByChatId(chatId)
            .filter { it.isValidTop() }
            .sortedWith(compareByDescending<FoloPidor> { it.score }.thenByDescending { it.lastWinDate })
            .take(10)

    fun getTopActive(chatId: Long, top: Int): List<FoloPidorWithCount>? =
        repo.getMessageCountInPeriod(
            chatId,
            LocalDate.now().toOffsetAtStartOfDay(),
            LocalDate.now().toOffsetAtEndOfDay(),
            top
        ).ifEmpty { null }

    fun getTopLiked(chatId: Long): List<FoloPidor> =
        repo.getTopLikedInPeriod(
            chatId,
            LocalDate.now().toOffsetAtStartOfDay(),
            LocalDate.now().toOffsetAtEndOfDay()
        )

    fun getTotalLikesInPeriod(chatId: Long): List<FoloPidorWithCount>? =
        repo.getTotalLikesInPeriod(
            chatId,
            LocalDate.now().toOffsetAtStartOfDay(),
            LocalDate.now().toOffsetAtEndOfDay()
        ).let { foloPidors ->
            if (foloPidors.isNotEmpty()) foloPidors.filter { it.count == foloPidors[0].count }
            else null
        }

    fun save(foloPidor: FoloPidor): FoloPidor = repo.save(foloPidor)

    fun delete(foloPidor: FoloPidor) = repo.delete(foloPidor)
}