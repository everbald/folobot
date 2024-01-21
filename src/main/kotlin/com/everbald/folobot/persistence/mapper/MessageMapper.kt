package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloMessage
import com.everbald.folobot.persistence.table.MessageTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

fun InsertStatement<Number>.toMessageInsert(message: FoloMessage) =
    this.apply {
        this[MessageTable.chatId] = message.chatId
        this[MessageTable.messageId] = message.messageId
        this[MessageTable.dateTime] = message.dateTime
        this[MessageTable.message] = message.message
    }

fun ResultRow.toMessage(): FoloMessage = FoloMessage(
    chatId = this[MessageTable.chatId],
    messageId = this[MessageTable.messageId],
    dateTime = this[MessageTable.dateTime],
    message = this[MessageTable.message],
    reactionCount = this[MessageTable.reactionCount]
)