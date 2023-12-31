package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloUser
import com.everbald.folobot.persistence.repo.FoloUserRepo
import org.springframework.stereotype.Component

@Component
class FoloUserService(private val foloUserRepo: FoloUserRepo) {
    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloUser> = foloUserRepo.getAll()

    /**
     * Чтение по Id
     * @param userId [Long] Id
     * @return [FoloUser]
     */
    fun find(userId: Long): FoloUser = foloUserRepo.find(userId) ?: FoloUser(userId)

    /**
     * Проверка наличия
     * @param userId Id пользователя
     * @return да/нет
     */
    fun exists(userId: Long): Boolean = foloUserRepo.exists(userId)

    /**
     * Сохранение
     * @param user [FoloUser]
     */
    fun save(user: FoloUser) = foloUserRepo.save(user)

    /**
     * Удаление
     * @param user [FoloUser]
     */
    fun delete(user: FoloUser) = foloUserRepo.delete(user.userId)
}