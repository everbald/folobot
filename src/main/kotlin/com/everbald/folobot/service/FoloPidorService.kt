package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.persistence.repo.FoloPidorRepo
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class FoloPidorService(
    private val foloPidorRepo: FoloPidorRepo,
    private val userService: UserService
) {

    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloPidor> =
        foloPidorRepo.getAll()
            .sortedWith(compareBy<FoloPidor> { it.chatId }.thenByDescending { it.score })

    /**
     * Чтение по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return [FoloPidor]
     */
    fun find(chatId: Long, userId: Long): FoloPidor = foloPidorRepo.find(chatId, userId) ?: FoloPidor(chatId, userId)

    /**
     * Проверка существования по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return да/нет
     */
    fun exists(chatId: Long, userId: Long): Boolean = foloPidorRepo.exists(chatId, userId)

    /**
     * Получение по Id чата
     * @param chatId Id чата
     * @return [<]
     */
    fun findByChatId(chatId: Long): List<FoloPidor> =
        foloPidorRepo.findByChatId(chatId)
            .sortedWith(compareBy<FoloPidor> { it.chatId }.thenByDescending { it.score })

    /**
     * Выбор случайного фолопидора
     *
     * @param chatId ID чата
     * @return [FoloPidor]
     */
    fun getRandom(chatId: Long): FoloPidor =
        foloPidorRepo.findByChatId(chatId)
            .filter { it.isAnchored() || (it.isValid() && userService.isInChat(it)) }
            .random()

    /**
     * Получение топ 10 фолопидоров чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTop(chatId: Long): List<FoloPidor> =
        foloPidorRepo.findByChatId(chatId)
            .filter { it.isValidTop() }
            .sortedWith(compareByDescending<FoloPidor> { it.score }.thenByDescending { it.lastWinDate })
            .take(10)

    /**
     * Паассивные фолопидоры чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getSlackers(chatId: Long): List<FoloPidor> =
        foloPidorRepo.findByChatId(chatId)
            .filter { userService.isInChat(it) && it.isValidSlacker() }
            .sortedBy { it.lastActiveDate }
            .take(10)

    /**
     * Получение списка андердогов
     * @param chatId Id чата
     * @return list of [FoloPidor]
     */
    fun getUnderdogs(chatId: Long): List<FoloPidor> =
        foloPidorRepo.findByChatId(chatId)
            .filter { userService.isInChat(it) && it.isValidUnderdog() }

    /**
     * Активные фолопидоры чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTopActive(chatId: Long): List<FoloPidor> =
        foloPidorRepo.findByChatId(chatId)
            .filter { it.lastActiveDate == LocalDate.now() }
            .sortedByDescending { it.messagesPerDay }
            .take(10)

    /**
     * Сохранение
     * @param foloPidor [FoloPidor]
     */
    fun save(foloPidor: FoloPidor) = foloPidorRepo.save(foloPidor)

    /**
     * Удаление
     * @param foloPidor[FoloPidor]
     */
    fun delete(foloPidor: FoloPidor) = foloPidorRepo.delete(foloPidor)
}