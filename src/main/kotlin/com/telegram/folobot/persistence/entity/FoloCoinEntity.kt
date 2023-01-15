package com.telegram.folobot.persistence.entity

import com.telegram.folobot.model.dto.FoloCoinDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "folo_coin")
class FoloCoinEntity(
    @Id
    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val points: Int,
    @Column(nullable = false)
    val coins: Int
)

fun FoloCoinEntity.toDto(): FoloCoinDto = FoloCoinDto(
    userId = this.userId,
    points = this.points,
    coins = this.coins
)