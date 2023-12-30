package com.everbald.folobot.domain

data class FoloCoin(
    val userId: Long,
    var points: Int = 0,
    var coins: Int = 0,
    var updated: Boolean = false
) {
    fun addPoints(points: Int): FoloCoin {
        this.points += points
        return this
    }

    fun addCoins(coins: Int): FoloCoin {
        this.coins += coins
        return this
    }

    fun calcCoins(threshold: Int): FoloCoin {
        this.coins++
        this.points -= threshold
        return this
    }
}