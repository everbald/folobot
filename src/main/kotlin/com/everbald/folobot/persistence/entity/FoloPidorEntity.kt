package com.everbald.folobot.persistence.entity

import com.everbald.folobot.model.dto.FoloPidorDto
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "folo_pidor")
class FoloPidorEntity(
    @EmbeddedId
    @Column(nullable = false)
    val id: FoloPidorId,

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    var foloUserEntity: FoloUserEntity,

    @Column(nullable = false)
    var score: Int,

    @Column(nullable = false)
    var lastWinDate: LocalDate,

    @Column(nullable = false)
    var lastActiveDate: LocalDate,

    @Column(nullable = false)
    var messagesPerDay: Int
)

fun FoloPidorEntity.toDto(): FoloPidorDto = FoloPidorDto(
    id = this.id,
    foloUser = this.foloUserEntity.toDto(),
    score = this.score,
    lastWinDate = this.lastWinDate,
    lastActiveDate = this.lastActiveDate,
    messagesPerDay = this.messagesPerDay
)