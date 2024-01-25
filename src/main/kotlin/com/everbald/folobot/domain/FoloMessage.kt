package com.everbald.folobot.domain

import org.telegram.telegrambots.meta.api.objects.Message
import java.time.OffsetDateTime

data class FoloMessage(
    val chatId: Long,
    val messageId: Int,
    val dateTime: OffsetDateTime,
    val message: Message,
    val reactionCount: Int = 0
)
