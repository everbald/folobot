package com.everbald.folobot.service

import com.everbald.folobot.model.dto.FoloUserDto
import com.everbald.folobot.model.dto.toEntity
import com.everbald.folobot.persistence.entity.FoloUserEntity
import com.everbald.folobot.persistence.entity.toDto
import com.everbald.folobot.persistence.repos.FoloUserRepo
import org.springframework.stereotype.Component

@Component
class FoloUserService(private val foloUserRepo: FoloUserRepo) {
    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloUserDto> {
        return foloUserRepo.findAll().map { it.toDto() }
    }

    /**
     * Чтение по Id
     * @param userId [Long] Id
     * @return [FoloUserDto]
     */
    fun findById(userId: Long): FoloUserDto {
        return foloUserRepo.findUserByUserId(userId)?.toDto() ?: FoloUserEntity(userId).toDto()
    }

    /**
     * Проверка наличия
     * @param userId Id пользователя
     * @return да/нет
     */
    fun existsById(userId: Long): Boolean {
        return foloUserRepo.existsById(userId)
    }

    /**
     * Сохранение
     * @param dto [FoloUserDto]
     */
    fun save(dto: FoloUserDto) {
        foloUserRepo.save(dto.toEntity())
    }

    /**
     * Удаление
     * @param dto [FoloUserDto]
     */
    fun delete(dto: FoloUserDto) {
        foloUserRepo.delete(dto.toEntity())
    }
}