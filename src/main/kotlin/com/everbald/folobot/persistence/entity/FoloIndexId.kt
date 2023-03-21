package com.everbald.folobot.persistence.entity

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.time.LocalDate

@Embeddable
data class FoloIndexId(
    val chatId: Long,
    val date: LocalDate = LocalDate.now()
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoloIndexId

        if (chatId != other.chatId) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}