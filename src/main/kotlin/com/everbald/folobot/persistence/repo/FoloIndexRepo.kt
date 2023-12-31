package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloIndex
import com.everbald.folobot.persistence.mapper.toFoloIndex
import com.everbald.folobot.persistence.mapper.toFoloIndexUpsert
import com.everbald.folobot.persistence.table.FoloIndexTable
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class FoloIndexRepo {
    fun save(index: FoloIndex): FoloIndex = transaction {
        FoloIndexTable
            .upsert { it.toFoloIndexUpsert(index) }
            .resultedValues
            ?.singleOrNull()
            ?.toFoloIndex()
            ?: throw RuntimeException("Couldn't save FoloIndex!")
    }

    fun find(chatId: Long, date: LocalDate): FoloIndex? = transaction {
        FoloIndexTable
            .select { FoloIndexTable.chatId eq chatId }
            .andWhere { FoloIndexTable.date eq date }
            .singleOrNull()
            ?.toFoloIndex()
    }

    fun getAveragePointsByChatId(chatId: Long, startDate: LocalDate, endDate: LocalDate): Double? = transaction {
        FoloIndexTable
            .slice(FoloIndexTable.points.avg())
            .select { FoloIndexTable.chatId eq chatId }
            .andWhere { FoloIndexTable.date.between(startDate, endDate) }
            .singleOrNull()
            ?.let { it[FoloIndexTable.points.avg()] }
            ?.toDouble()
    }

    fun getAverageIndexByChatId(chatId: Long, startDate: LocalDate, endDate: LocalDate): Double? = transaction {
        FoloIndexTable
            .slice(FoloIndexTable.index.avg())
            .select { FoloIndexTable.chatId eq chatId }
            .andWhere { FoloIndexTable.date.between(startDate, endDate) }
            .singleOrNull()
            ?.let { it[FoloIndexTable.index.avg()] }
            ?.toDouble()
    }

    fun findByChatIdAndDate(chatId: Long, startDate: LocalDate, endDate: LocalDate): List<FoloIndex> = transaction {
        FoloIndexTable
            .select { FoloIndexTable.chatId eq chatId }
            .andWhere { FoloIndexTable.date.between(startDate, endDate) }
            .orderBy(FoloIndexTable.date)
            .map { it.toFoloIndex() }
    }
}