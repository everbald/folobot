package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloVar
import com.everbald.folobot.persistence.table.FoloVarTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpsertStatement

fun UpsertStatement<Long>.toFoloVarUpsert(foloVar: FoloVar) =
    this.apply {
        this[FoloVarTable.chatId] = foloVar.chatId
        this[FoloVarTable.type] = foloVar.type
        this[FoloVarTable.value] = foloVar.value
    }

fun ResultRow.toFoloVar(): FoloVar = FoloVar(
    chatId = this[FoloVarTable.chatId],
    type = this[FoloVarTable.type],
    value = this[FoloVarTable.value]
)