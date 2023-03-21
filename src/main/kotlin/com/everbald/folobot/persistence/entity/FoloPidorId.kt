package com.everbald.folobot.persistence.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class FoloPidorId(
    var chatId: Long,
    var userId: Long
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoloPidorId

        if (chatId != other.chatId) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}