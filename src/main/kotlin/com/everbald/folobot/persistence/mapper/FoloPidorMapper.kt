package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.persistence.table.FoloPidorTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpsertStatement

fun UpsertStatement<Long>.toFoloPidorUpsert(foloPidor: FoloPidor) =
    this.apply {
        this[FoloPidorTable.chatId] = foloPidor.chatId
        this[FoloPidorTable.userId] = foloPidor.user.userId
        this[FoloPidorTable.score] = foloPidor.score
        this[FoloPidorTable.lastWinDate] = foloPidor.lastWinDate
        this[FoloPidorTable.lastActiveDate] = foloPidor.lastActiveDate
        this[FoloPidorTable.messagesPerDay] = foloPidor.messagesPerDay
    }

fun ResultRow.toFoloPidor(): FoloPidor = FoloPidor(
    chatId = this[FoloPidorTable.chatId],
    user = this.toFoloUser(),
    score = this[FoloPidorTable.score],
    lastWinDate = this[FoloPidorTable.lastWinDate],
    lastActiveDate = this[FoloPidorTable.lastActiveDate],
    messagesPerDay = this[FoloPidorTable.messagesPerDay]
)