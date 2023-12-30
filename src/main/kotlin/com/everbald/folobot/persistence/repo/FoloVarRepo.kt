package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloVar
import com.everbald.folobot.domain.type.VarType
import com.everbald.folobot.persistence.mapper.toFoloVar
import com.everbald.folobot.persistence.mapper.toFoloVarUpsert
import com.everbald.folobot.persistence.table.FoloVarTable
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

@Repository
class FoloVarRepo {
    fun save(foloVar: FoloVar): FoloVar = transaction {
        FoloVarTable
            .upsert { it.toFoloVarUpsert(foloVar) }
            .resultedValues
            ?.singleOrNull()
            ?.toFoloVar()
            ?: throw RuntimeException("Couldn't save FoloVar!")
    }

    fun find(chatId: Long, type: VarType): FoloVar? = transaction {
        FoloVarTable
            .select { FoloVarTable.chatId eq chatId }
            .andWhere { FoloVarTable.type eq type }
            .singleOrNull()
            ?.toFoloVar()
    }
}