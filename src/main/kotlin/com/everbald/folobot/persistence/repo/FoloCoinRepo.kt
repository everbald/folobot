package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloCoin
import com.everbald.folobot.persistence.mapper.toFoloCoin
import com.everbald.folobot.persistence.mapper.toFoloCoinUpsert
import com.everbald.folobot.persistence.table.FoloCoinTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

@Repository
class FoloCoinRepo {
    fun save(foloCoin: FoloCoin): FoloCoin = transaction {
        FoloCoinTable
            .upsert { it.toFoloCoinUpsert(foloCoin) }
            .resultedValues
            ?.singleOrNull()
            ?.toFoloCoin()
            ?: throw RuntimeException("Couldn't save FoloCoin!")
    }

    fun findCoinByUserId(userId: Long): FoloCoin? = transaction {
        FoloCoinTable
            .select { FoloCoinTable.userId eq userId }
            .singleOrNull()
            ?.toFoloCoin()
    }
    fun findTop10ByOrderByCoinsDescPointsDesc(): List<FoloCoin> = transaction {
        FoloCoinTable
            .selectAll()
            .orderBy(Pair(FoloCoinTable.coins, SortOrder.DESC), Pair(FoloCoinTable.points, SortOrder.DESC))
            .limit(10)
            .map { it.toFoloCoin() }
    }
    fun findByPointsGreaterThanEqual(points: Int): List<FoloCoin> = transaction {
        FoloCoinTable
            .select { FoloCoinTable.points greaterEq points }
            .map { it.toFoloCoin() }
    }

    fun getSumCoins(): Int? = transaction {
        FoloCoinTable
            .slice(FoloCoinTable.coins.sum())
            .selectAll()
            .singleOrNull()
            ?.let { it[FoloCoinTable.coins.sum()] }
    }
}