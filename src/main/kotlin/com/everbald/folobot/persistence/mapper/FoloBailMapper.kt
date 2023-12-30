package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloBail
import com.everbald.folobot.persistence.table.FoloBailTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

fun InsertStatement<Number>.toFoloBailInsert(bail: FoloBail) =
    this.apply {
        this[FoloBailTable.chatId] = bail.chatId
        this[FoloBailTable.dateTime] = bail.dateTime
        this[FoloBailTable.message] = bail.message
    }

fun ResultRow.toFoloBail(): FoloBail = FoloBail(
    chatId = this[FoloBailTable.chatId],
    dateTime = this[FoloBailTable.dateTime],
    message = this[FoloBailTable.message]
)