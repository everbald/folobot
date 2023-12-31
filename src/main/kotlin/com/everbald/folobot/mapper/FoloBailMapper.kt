package com.everbald.folobot.mapper

import com.everbald.folobot.domain.FoloBail
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.OffsetDateTime

fun Message.toFoloBail(): FoloBail =
    FoloBail(
        chatId = this.chatId,
        dateTime = OffsetDateTime.now(),
        message = this
    )