package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloBail
import com.everbald.folobot.persistence.mapper.toFoloBail
import com.everbald.folobot.persistence.mapper.toFoloBailInsert
import com.everbald.folobot.persistence.table.FoloBailTable
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class FoloBailRepo {
    fun save(bail: FoloBail): UUID = transaction {
        FoloBailTable
            .insert { it.toFoloBailInsert(bail) }
            .resultedValues
            ?.singleOrNull()
            ?.let { it[FoloBailTable.id] }
            ?: throw RuntimeException("Couldn't save FoloBail!")
    }

    fun getInInterval(chatId: Long, startDateTime: OffsetDateTime, endDateTime: OffsetDateTime): List<FoloBail> =
        transaction {
            FoloBailTable
                .select { FoloBailTable.chatId eq chatId }
                .andWhere { FoloBailTable.dateTime.between(startDateTime, endDateTime) }
                .map { it.toFoloBail() }
        }
}