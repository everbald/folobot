package com.everbald.folobot.persistence.entity

import com.everbald.folobot.model.dto.FoloIndexDto
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "folo_index")
class FoloIndexEntity(
    @EmbeddedId
    @Column(nullable = false)
    val id: FoloIndexId,
    @Column(nullable = false)
    val points: Int,
    val index: Double?
)

fun FoloIndexEntity.toDto(): FoloIndexDto = FoloIndexDto(
    id = this.id,
    points = this.points,
    index = this.index
)