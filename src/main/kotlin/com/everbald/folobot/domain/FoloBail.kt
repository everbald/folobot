package com.everbald.folobot.domain

import org.telegram.telegrambots.meta.api.objects.Message
import java.time.OffsetDateTime

data class FoloBail(
    val chatId: Long,
    val dateTime: OffsetDateTime,
    val message: Message
)
