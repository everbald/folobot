package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.domain.FoloPidorWithMessageCount
import com.everbald.folobot.persistence.table.FoloPidorTable
import com.everbald.folobot.persistence.table.MessageTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.statements.UpsertStatement

fun UpsertStatement<Long>.toFoloPidorUpsert(foloPidor: FoloPidor) =
    this.apply {
        this[FoloPidorTable.chatId] = foloPidor.chatId
        this[FoloPidorTable.userId] = foloPidor.user.userId
        this[FoloPidorTable.score] = foloPidor.score
        foloPidor.lastWinDate?.let { this[FoloPidorTable.lastWinDate] = it }
    }

fun ResultRow.toFoloPidor(): FoloPidor = FoloPidor(
    chatId = this[FoloPidorTable.chatId],
    user = this.toFoloUser(),
    score = this[FoloPidorTable.score],
    lastWinDate = this[FoloPidorTable.lastWinDate],
)

fun ResultRow.toFoloPidorWithMessageCount(): FoloPidorWithMessageCount = FoloPidorWithMessageCount(
    foloPidor = this.toFoloPidor(),
    messageCount = this[MessageTable.messageId.count().over().partitionBy(MessageTable.userId)].toInt()
)

