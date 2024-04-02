package com.everbald.folobot.domain

import org.telegram.telegrambots.meta.api.objects.message.Message
import java.time.OffsetDateTime

data class FoloMessage(
    val chatId: Long,
    val userId: Long,
    val messageId: Int,
    val dateTime: OffsetDateTime,
    val message: Message,
    val reactionCount: Int = 0
)
