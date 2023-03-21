package com.everbald.folobot.persistence.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class FoloVarId(
    private val chatId: Long,
    private val type: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoloVarId

        if (chatId != other.chatId) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}