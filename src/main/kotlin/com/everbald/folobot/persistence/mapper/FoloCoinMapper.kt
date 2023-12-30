package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.FoloCoin
import com.everbald.folobot.persistence.table.FoloCoinTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpsertStatement

fun UpsertStatement<Long>.toFoloCoinUpsert(coin: FoloCoin) =
    this.apply {
        this[FoloCoinTable.userId] = coin.userId
        this[FoloCoinTable.points] = coin.points
        this[FoloCoinTable.coins] = coin.points
    }

fun ResultRow.toFoloCoin(): FoloCoin = FoloCoin(
    userId = this[FoloCoinTable.userId],
    points = this[FoloCoinTable.points],
    coins = this[FoloCoinTable.coins],
)