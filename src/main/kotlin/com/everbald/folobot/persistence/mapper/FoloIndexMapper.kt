package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloIndex
import com.everbald.folobot.persistence.table.FoloIndexTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpsertStatement

fun UpsertStatement<Long>.toFoloIndexUpsert(index: FoloIndex) =
    this.apply {
        this[FoloIndexTable.chatId] = index.chatId
        this[FoloIndexTable.date] = index.date
        this[FoloIndexTable.points] = index.points
        this[FoloIndexTable.index] = index.index

    }

fun ResultRow.toFoloIndex(): FoloIndex = FoloIndex(
    chatId = this[FoloIndexTable.chatId],
    date = this[FoloIndexTable.date],
    points = this[FoloIndexTable.points],
    index = this[FoloIndexTable.index]
)