package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.domain.FoloPidorWithMessageCount
import com.everbald.folobot.persistence.mapper.toFoloPidor
import com.everbald.folobot.persistence.mapper.toFoloPidorUpsert
import com.everbald.folobot.persistence.mapper.toFoloPidorWithMessageCount
import com.everbald.folobot.persistence.table.FoloPidorTable
import com.everbald.folobot.persistence.table.FoloUserTable
import com.everbald.folobot.persistence.table.MessageTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class FoloPidorRepo {
    fun save(foloPidor: FoloPidor): FoloPidor = transaction {
        FoloPidorTable
            .upsert { it.toFoloPidorUpsert(foloPidor) }
            .resultedValues
            ?.singleOrNull()
            ?.let { foloPidor }
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
            .andWhere { FoloPidorTable.userId eq userId }
            .singleOrNull()
            ?.toFoloPidor()
    }

    fun find(chatId: Long, userIds: List<Long>): List<FoloPidor> = transaction {
        FoloPidorTable
            .joinFoloUserTable()
            .select { FoloPidorTable.chatId eq chatId }
            .andWhere { FoloPidorTable.userId inList userIds }
            .map { it.toFoloPidor() }
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

    fun getMessageCountInPeriod(
        chatId: Long,
        startDateTime: OffsetDateTime,
        endDateTime: OffsetDateTime,
        limit: Int,
    ): List<FoloPidorWithMessageCount> =
        transaction {
            MessageTable
                .joinFoloPidorTable()
                .slice(
                    FoloPidorTable.fields
                        .plus(FoloUserTable.fields)
                        .plus(MessageTable.messageId.count().over().partitionBy(MessageTable.userId))
                )
                .select { MessageTable.chatId eq chatId }
                .andWhere { MessageTable.dateTime.between(startDateTime, endDateTime) }
                .withDistinct()
                .orderBy(MessageTable.messageId.count().over().partitionBy(MessageTable.userId), SortOrder.DESC)
                .limit(limit)
                .map { it.toFoloPidorWithMessageCount() }
        }

    fun getTopLikedInPeriod(
        chatId: Long,
        startDateTime: OffsetDateTime,
        endDateTime: OffsetDateTime,
    ): List<FoloPidor> =
        transaction {
            MessageTable
                .joinFoloPidorTable()
                .slice(
                    FoloPidorTable.fields
                        .plus(FoloUserTable.fields)
                )
                .select { MessageTable.chatId eq chatId }
                .andWhere { MessageTable.dateTime.between(startDateTime, endDateTime) }
                .andWhere {
                    MessageTable.reactionCount eq MessageTable.getMaxLikesInPeriod(chatId, startDateTime, endDateTime)
                }
                .withDistinct()
                .map { it.toFoloPidor() }
        }

    private fun MessageTable.getMaxLikesInPeriod(
        chatId: Long,
        startDateTime: OffsetDateTime,
        endDateTime: OffsetDateTime,
    ): Int =
        this.slice(MessageTable.reactionCount.max().alias("MaxReactionCount"))
            .select { MessageTable.chatId eq chatId }
            .andWhere { dateTime.between(startDateTime, endDateTime) }
            .single()[MessageTable.reactionCount.max().alias("MaxReactionCount")] ?: 0

    private fun FoloPidorTable.joinFoloUserTable() =
        this.innerJoin(FoloUserTable)

    private fun MessageTable.joinFoloPidorTable() =
        this.join(otherTable = FoloPidorTable, joinType = JoinType.LEFT) {
            (chatId eq FoloPidorTable.chatId).and(userId eq FoloPidorTable.userId)
        }.innerJoin(FoloUserTable)
}