package com.everbald.folobot.model.dto

import com.everbald.folobot.persistence.entity.FoloIndexEntity
import com.everbald.folobot.persistence.entity.FoloIndexId
import java.time.LocalDate

data class FoloIndexDto(
    val id: FoloIndexId,
    var points: Int = 0,
    var index: Double? = null
) {
    constructor(chatId: Long, date:LocalDate) : this(FoloIndexId(chatId, date))
    fun addPoints(points: Int): FoloIndexDto {
        this.points += points
        return this
    }

    fun setIndex(index: Double): FoloIndexDto {
        this.index = index
        return this
    }
}

fun FoloIndexDto.toEntity(): FoloIndexEntity = FoloIndexEntity(
    id = this.id,
    points = this.points,
    index = this.index
)