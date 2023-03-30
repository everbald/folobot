package com.everbald.folobot.persistence.repos

import com.everbald.folobot.persistence.entity.FoloIndexEntity
import com.everbald.folobot.persistence.entity.FoloIndexId
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface FoloIndexRepo : CrudRepository<FoloIndexEntity, FoloIndexId> {
    fun findIndexById(id: FoloIndexId): FoloIndexEntity?
    @Query(value = "select avg(points) from folo_index where chat_id = ?1 and date between ?2 and ?3", nativeQuery = true)
    fun getAveragePointsByIdChatId(chatId: Long, startDate: LocalDate, endDate: LocalDate): Double?

    @Query(value = "select avg(index) from folo_index where chat_id = ?1 and date between ?2 and ?3", nativeQuery = true)
    fun getAverageIndexByIdChatId(chatId: Long, startDate: LocalDate, endDate: LocalDate): Double?

    fun findByIdChatIdAndIdDateBetweenOrderByIdDate(chatId: Long, startDate: LocalDate, endDate: LocalDate): List<FoloIndexEntity>
}