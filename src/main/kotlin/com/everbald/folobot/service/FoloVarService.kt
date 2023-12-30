package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloVar
import com.everbald.folobot.domain.type.VarType
import com.everbald.folobot.persistence.repo.FoloVarRepo
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class FoloVarService(private val foloVarRepo: FoloVarRepo) {
    companion object {
        val COMMON_CHATID = 0L
        val INITIAL_USERID = 0L
    }

    /**
     * Дата последнего определения фолопидора
     *
     * @param chatId ID чата
     * @return [LocalDate]
     */
    fun getLastFolopidorDate(chatId: Long): LocalDate =
        LocalDate.parse(
            foloVarRepo.find(chatId, VarType.LAST_FOLOPIDOR_DATE)?.value
                ?: "2020-01-01"
        )

    /**
     * Сохранить дату последнего определения фолопидора
     *
     * @param chatId ID чата
     * @param value [LocalDate] Дата
     */
    fun setLastFolopidorDate(chatId: Long, value: LocalDate): FoloVar =
        FoloVar(chatId, VarType.LAST_FOLOPIDOR_DATE, value)
            .let { foloVarRepo.save(it) }

    /**
     * Последний фолопидор
     *
     * @param chatId ID чата
     * @return [Long] userid
     */
    fun getLastFolopidorWinner(chatId: Long): Long =
        foloVarRepo.find(chatId, VarType.LAST_FOLOPIDOR_USERID)?.value?.toLong()
            ?: INITIAL_USERID

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatId ID чата
     * @param userId  [Long] Id пользователя
     */
    fun setLastFolopidorWinner(chatId: Long, userId: Long): FoloVar =
        FoloVar(chatId, VarType.LAST_FOLOPIDOR_USERID, userId)
            .let { foloVarRepo.save(it) }

    /**
     * Дата последнего фапа
     * @return [LocalDate]
     */
    fun getLastFapDate(): LocalDate =
        LocalDate.parse(
            foloVarRepo.find(COMMON_CHATID, VarType.LAST_FAP_DATE)?.value
                ?: "2020-01-01"
        )

    /**
     * Сохранить дату последнего фапа
     *
     * @param fapDate [LocalDate] Дата
     */
    fun setLastFapDate(fapDate: LocalDate): FoloVar =
        FoloVar(COMMON_CHATID, VarType.LAST_FAP_DATE, fapDate)
            .let { foloVarRepo.save(it) }

    /**
     * Счетчик фап запросов
     *
     * @param chatId ID чата
     * @return [Integer] Счетчик
     */
    fun getNoFapCount(chatId: Long): Int =
        ((foloVarRepo.find(chatId, VarType.LAST_FAP_COUNT)?.value?.toInt() ?: 0) + 1)
            .also { setNoFapCount(chatId, it) }

    /**
     * Сохранить счетчик фап запросов
     *
     * @param chatId ID чата
     * @param value  [Integer] Счетчик
     */
    fun setNoFapCount(chatId: Long, value: Int): FoloVar =
        FoloVar(chatId, VarType.LAST_FAP_COUNT, value)
            .let { foloVarRepo.save(it) }

    /**
     *  Фолопидор предыдущего года
     */
    fun getLastYearFolopidor(chatId: Long): Long =
        foloVarRepo.find(chatId, VarType.LAST_YEAR_FOLOPIDOR_USERID)?.value?.toLong()
            ?: INITIAL_USERID

    /**
     * Сохранить фолопидора предыдущего года
     */
    fun setLastYearFolopidor(chatId: Long, userId: Long): FoloVar =
        FoloVar(chatId, VarType.LAST_YEAR_FOLOPIDOR_USERID, userId)
            .let { foloVarRepo.save(it) }
}