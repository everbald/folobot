package com.everbald.folobot.domain

import java.time.LocalDate

data class FoloIndex(
    val chatId: Long,
    val date: LocalDate,
    var points: Int = 0,
    var index: Double? = null
) {
    fun addPoints(points: Int): FoloIndex {
        this.points += points
        return this
    }

    fun setIndex(index: Double): FoloIndex {
        this.index = index
        return this
    }
}