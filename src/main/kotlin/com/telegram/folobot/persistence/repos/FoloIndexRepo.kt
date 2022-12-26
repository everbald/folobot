package com.telegram.folobot.persistence.repos

import com.telegram.folobot.persistence.entity.FoloIndexEntity
import com.telegram.folobot.persistence.entity.FoloIndexId
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface FoloIndexRepo : CrudRepository<FoloIndexEntity, FoloIndexId> {
    fun findIndexById(id: FoloIndexId): FoloIndexEntity?
    @Query(value = "select avg(points) from folo_index where chat_id = ?1 and date between ?2 and ?3", nativeQuery = true)
    fun getAveragePointsByIdChatId(chatId: Long, startDate: LocalDate, endDate: LocalDate): Double?
}