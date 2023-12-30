package com.everbald.folobot.domain

import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDateTime

data class MessageQueue(
    val recievedAt: LocalDateTime,
    val message: Message,
    var backupMessage: Message? = null,
    var restored: Boolean = false
)