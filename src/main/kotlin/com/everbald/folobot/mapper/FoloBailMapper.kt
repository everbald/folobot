package com.everbald.folobot.mapper

import com.everbald.folobot.domain.FoloMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import java.time.OffsetDateTime

fun Message.toFoloMessage(): FoloMessage =
    FoloMessage(
        chatId = this.chatId,
        userId = this.from.id,
        messageId = this.messageId,
        dateTime = OffsetDateTime.now(),
        message = this
    )