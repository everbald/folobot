package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.persistence.mapper.toFoloPidor
import com.everbald.folobot.persistence.mapper.toFoloPidorUpsert
import com.everbald.folobot.persistence.table.FoloPidorTable
import com.everbald.folobot.persistence.table.FoloUserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

@Repository
class FoloPidorRepo {
    fun save(foloPidor: FoloPidor) = transaction {
        FoloPidorTable
            .upsert { it.toFoloPidorUpsert(foloPidor) }
            .resultedValues
            ?.singleOrNull()
            ?: throw RuntimeException("Couldn't save FoloPidor!")
    }

    fun getAll(): List<FoloPidor> = transaction {
        FoloPidorTable
            .joinFoloUserTable()
            .selectAll()
            .map { it.toFoloPidor() }
    }

    fun exists(chatId: Long, userId: Long) = transaction {
        !FoloPidorTable
            .slice(FoloPidorTable.userId, FoloPidorTable.userId)
            .select { FoloPidorTable.chatId eq chatId }
            .andWhere { FoloUserTable.userId eq userId }
            .empty()
    }

    fun find(chatId: Long, userId: Long): FoloPidor? = transaction {
        FoloPidorTable
            .joinFoloUserTable()
            .select { FoloPidorTable.chatId eq chatId }
            .andWhere { FoloPidorTable.userId eq userId}
            .singleOrNull()
            ?.toFoloPidor()
}

    fun findByChatId(chatId: Long): List<FoloPidor> = transaction {
        FoloPidorTable
            .joinFoloUserTable()
            .select { FoloPidorTable.chatId eq chatId }
            .map { it.toFoloPidor() }
    }

    fun delete(foloPidor: FoloPidor) = transaction {
        FoloUserTable
            .deleteWhere {
                (FoloPidorTable.chatId eq foloPidor.chatId)
                    .and(FoloPidorTable.userId eq foloPidor.user.userId)
            }
    }

    private fun FoloPidorTable.joinFoloUserTable() =
        this.innerJoin(FoloUserTable)
}