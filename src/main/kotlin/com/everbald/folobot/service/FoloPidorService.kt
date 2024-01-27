package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.domain.FoloPidorWithMessageCount
import com.everbald.folobot.persistence.repo.FoloPidorRepo
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class FoloPidorService(private val repo: FoloPidorRepo) {
    private val logger: KLogger = KotlinLogging.logger { this::class.java }

    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloPidor> =
        repo.getAll()
            .sortedWith(compareBy<FoloPidor> { it.chatId }.thenByDescending { it.score })

    /**
     * Чтение по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return [FoloPidor]
     */
    fun find(chatId: Long, userId: Long): FoloPidor = repo.find(chatId, userId) ?: FoloPidor(chatId, userId)

    /**
     * Проверка существования по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return да/нет
     */
    fun exists(chatId: Long, userId: Long): Boolean = repo.exists(chatId, userId)

    /**
     * Получение по Id чата
     * @param chatId Id чата
     * @return [<]
     */
    fun findByChatId(chatId: Long): List<FoloPidor> =
        repo.findByChatId(chatId)
            .sortedWith(compareBy<FoloPidor> { it.chatId }.thenByDescending { it.score })

    /**
     * Получение топ 10 фолопидоров чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTop(chatId: Long): List<FoloPidor> =
        repo.findByChatId(chatId)
            .filter { it.isValidTop() }
            .sortedWith(compareByDescending<FoloPidor> { it.score }.thenByDescending { it.lastWinDate })
            .take(10)

    fun getTopActive(chatId: Long, top: Int): List<FoloPidorWithMessageCount> =
        repo.getMessageCountInPeriod(
            chatId,
            LocalDate.now().toOffsetAtStartOfDay(),
            LocalDate.now().toOffsetAtEndOfDay(),
            top
        )

    fun getTopLiked(chatId: Long): List<FoloPidor> =
        repo.getTopLikedInPeriod(chatId,
            LocalDate.now().toOffsetAtStartOfDay(),
            LocalDate.now().toOffsetAtEndOfDay()
        )

    fun save(foloPidor: FoloPidor): FoloPidor = repo.save(foloPidor)

    fun delete(foloPidor: FoloPidor) = repo.delete(foloPidor)
}