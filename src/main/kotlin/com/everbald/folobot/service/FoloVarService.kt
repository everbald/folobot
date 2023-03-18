package com.everbald.folobot.service

import com.everbald.folobot.model.VarTypeEnum
import com.everbald.folobot.persistence.entity.FoloVarEntity
import com.everbald.folobot.persistence.entity.FoloVarId
import com.everbald.folobot.persistence.repos.FoloVarRepo
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
    fun getLastFolopidorDate(chatId: Long): LocalDate {
        return LocalDate.parse(
            foloVarRepo.findVarById(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_DATE.name))?.value
                ?: "2020-01-01"
        )
    }

    /**
     * Сохранить дату последнего определения фолопидора
     *
     * @param chatId ID чата
     * @param value [LocalDate] Дата
     */
    fun setLastFolopidorDate(chatId: Long, value: LocalDate) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_DATE.name), value.toString()))
    }

    /**
     * Последний фолопидор
     *
     * @param chatId ID чата
     * @return [Long] userid
     */
    fun getLastFolopidorWinner(chatId: Long): Long {
        return foloVarRepo.findVarById(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_USERID.name))?.value?.toLong()
            ?: INITIAL_USERID
    }

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatId ID чата
     * @param userId  [Long] Id пользователя
     */
    fun setLastFolopidorWinner(chatId: Long, userId: Long) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_USERID.name), userId.toString()))
    }

    /**
     * Дата последнего фапа
     * @return [LocalDate]
     */
    fun getLastFapDate(): LocalDate {
        return LocalDate.parse(
            foloVarRepo.findVarById(FoloVarId(COMMON_CHATID, VarTypeEnum.LAST_FAP_DATE.name))?.value
                ?: "2020-01-01"
        )
    }

    /**
     * Сохранить дату последнего фапа
     *
     * @param fapDate [LocalDate] Дата
     */
    fun setLastFapDate(fapDate: LocalDate) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(COMMON_CHATID, VarTypeEnum.LAST_FAP_DATE.name), fapDate.toString()))
    }

    /**
     * Счетчик фап запросов
     *
     * @param chatId ID чата
     * @return [Integer] Счетчик
     */
    fun getNoFapCount(chatId: Long): Int {
        val noFapCount =
            (foloVarRepo.findVarById(FoloVarId(chatId, VarTypeEnum.LAST_FAP_COUNT.name))?.value?.toInt() ?: 0) + 1
        setNoFapCount(chatId, noFapCount)
        return noFapCount
    }

    /**
     * Сохранить счетчик фап запросов
     *
     * @param chatId ID чата
     * @param value  [Integer] Счетчик
     */
    fun setNoFapCount(chatId: Long, value: Int) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(chatId, VarTypeEnum.LAST_FAP_COUNT.name), value.toString()))
    }

    /**
     *  Фолопидор предыдущего года
     */
    fun getLastYearFolopidor(chatId: Long): Long {
        return foloVarRepo.findVarById(FoloVarId(chatId, VarTypeEnum.LAST_YEAR_FOLOPIDOR_USERID.name))?.value?.toLong()
            ?: INITIAL_USERID
    }

    /**
     * Сохранить фолопидора предыдущего года
     */
    fun setLastYearFolopidor(chatId: Long, userId: Long) {
        foloVarRepo.save(
            FoloVarEntity(
                FoloVarId(chatId, VarTypeEnum.LAST_YEAR_FOLOPIDOR_USERID.name),
                userId.toString()
            )
        )
    }
}