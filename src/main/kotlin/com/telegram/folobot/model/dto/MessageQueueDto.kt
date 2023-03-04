package com.telegram.folobot.model.dto

import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDateTime

data class MessageQueueDto(
    val recievedAt: LocalDateTime,
    val message: Message,
    var backupMessage: Message? = null,
    var restored: Boolean = false
)