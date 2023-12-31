package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloUser
import com.everbald.folobot.persistence.mapper.toFoloUser
import com.everbald.folobot.persistence.mapper.toFoloUserUpsert
import com.everbald.folobot.persistence.table.FoloPidorTable
import com.everbald.folobot.persistence.table.FoloUserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

@Repository
class FoloUserRepo {
    fun save(user: FoloUser): FoloUser = transaction {
        FoloUserTable
            .upsert { it.toFoloUserUpsert(user) }
            .resultedValues
            ?.singleOrNull()
            ?.toFoloUser()
            ?: throw RuntimeException("Couldn't save FoloUser!")
    }

    fun find(userId: Long): FoloUser? = transaction {
        FoloUserTable
            .select { FoloUserTable.userId eq userId }
            .singleOrNull()
            ?.toFoloUser()
    }

    fun getAll(): List<FoloUser> = transaction { FoloUserTable.selectAll().map { it.toFoloUser() } }

    fun exists(userId: Long) = transaction {
        !FoloUserTable
            .slice(FoloUserTable.userId)
            .select { FoloUserTable.userId eq userId }
            .empty()
    }

    fun delete(userId: Long) = transaction {
        FoloPidorTable.deleteWhere { FoloPidorTable.userId eq userId }
        FoloUserTable.deleteWhere { FoloUserTable.userId eq userId }
    }
}