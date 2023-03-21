package com.everbald.folobot.service

import com.everbald.folobot.model.dto.FoloPidorDto
import com.everbald.folobot.model.dto.toEntity
import com.everbald.folobot.persistence.entity.FoloPidorId
import com.everbald.folobot.persistence.entity.toDto
import com.everbald.folobot.persistence.repos.FoloPidorRepo
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
    fun findAll(): List<FoloPidorDto> {
        return foloPidorRepo.findAll()
            .map { it.toDto() }
            .sortedWith(compareBy<FoloPidorDto> { it.id.chatId }.thenByDescending { it.score })
    }

    /**
     * Чтение по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return [FoloPidorDto]
     */
    fun findById(chatId: Long, userId: Long): FoloPidorDto {
        return foloPidorRepo.findFoloPidorById(FoloPidorId(chatId, userId))?.toDto()
            ?: FoloPidorDto(chatId, userId)
    }

    /**
     * Проверка существования по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return да/нет
     */
    fun existsById(chatId: Long, userId: Long): Boolean {
        return foloPidorRepo.existsById(FoloPidorId(chatId, userId))
    }

    /**
     * Получение по Id чата
     * @param chatId Id чата
     * @return [<]
     */
    fun findByIdChatId(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { it.toDto() }
            .sortedWith(compareBy<FoloPidorDto> { it.id.chatId }.thenByDescending { it.score })
    }

    /**
     * Выбор случайного фолопидора
     *
     * @param chatId ID чата
     * @return [FoloPidorDto]
     */
    fun getRandom(chatId: Long): FoloPidorDto {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { it.toDto() }
            .filter { it.isAnchored() || (it.isValid() && userService.isInChat(it, chatId)) }
            .random()
    }

    /**
     * Получение топ 10 фолопидоров чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTop(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { it.toDto() }
            .filter { it.isValidTop() }
            .sortedWith(compareByDescending<FoloPidorDto> { it.score }.thenByDescending { it.lastWinDate })
            .take(10)
    }

    /**
     * Паассивные фолопидоры чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getSlackers(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { it.toDto() }
            .filter { userService.isInChat(it, chatId) && it.isValidSlacker() }
            .sortedBy { it.lastActiveDate }
            .take(10)
    }

    /**
     * Получение списка андердогов
     * @param chatId Id чата
     * @return list of [FoloPidorDto]
     */
    fun getUnderdogs(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { it.toDto() }
            .filter { userService.isInChat(it, chatId) && it.isValidUnderdog() }
    }

    /**
     * Активные фолопидоры чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTopActive(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { it.toDto() }
            .filter { it.lastActiveDate == LocalDate.now() }
            .sortedByDescending { it.messagesPerDay }
            .take(10)
    }

    /**
     * Сохранение
     * @param dto [FoloPidorDto]
     */
    fun save(dto: FoloPidorDto) {
        foloPidorRepo.save(dto.toEntity())
    }

    /**
     * Удаление
     * @param dto [FoloPidorDto]
     */
    fun delete(dto: FoloPidorDto) {
        foloPidorRepo.delete(dto.toEntity())
    }
}