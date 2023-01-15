package com.telegram.folobot.model.dto

import com.telegram.folobot.persistence.entity.FoloCoinEntity

data class FoloCoinDto(
    val userId: Long,
    var points: Int = 0,
    var coins: Int = 0,
    var updated: Boolean = false
) {
    fun addPoints(points: Int): FoloCoinDto {
        this.points += points
        return this
    }

    fun calcCoins(threshold: Int): FoloCoinDto {
        this.coins++
        this.points -= threshold
        return this
    }
}

fun FoloCoinDto.toEntity(): FoloCoinEntity = FoloCoinEntity(
    userId = this.userId,
    points = this.points,
    coins = this.coins
)