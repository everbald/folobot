package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloUser
import com.everbald.folobot.persistence.table.FoloUserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpsertStatement

fun UpsertStatement<Long>.toFoloUserUpsert(user: FoloUser) =
    this.apply {
        this[FoloUserTable.userId] = user.userId
        this[FoloUserTable.mainId] = user.mainId
        this[FoloUserTable.name] = user.name
        this[FoloUserTable.tag] = user.tag
        this[FoloUserTable.anchor] = user.anchor
    }

fun ResultRow.toFoloUser(): FoloUser = FoloUser(
    userId = this[FoloUserTable.userId],
    mainId = this[FoloUserTable.mainId],
    name = this[FoloUserTable.name],
    tag = this[FoloUserTable.tag],
    anchor = this[FoloUserTable.anchor],
)