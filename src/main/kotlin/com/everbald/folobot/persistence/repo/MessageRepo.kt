package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.FoloMessage
import com.everbald.folobot.persistence.mapper.toMessage
import com.everbald.folobot.persistence.mapper.toMessageInsert
import com.everbald.folobot.persistence.table.MessageTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class MessageRepo {
    fun save(message: FoloMessage): UUID = transaction {
        MessageTable
            .insert { it.toMessageInsert(message) }
            .resultedValues
            ?.singleOrNull()
            ?.let { it[MessageTable.id] }
            ?: throw RuntimeException("Couldn't save FoloMessage!")
    }

    fun deleteBefore(dateTime: OffsetDateTime): Int = transaction {
        MessageTable.deleteWhere { MessageTable.dateTime lessEq dateTime }
    }

    fun getInInterval(chatId: Long, startDateTime: OffsetDateTime, endDateTime: OffsetDateTime): List<FoloMessage> =
        transaction {
            MessageTable
                .select { MessageTable.chatId eq chatId }
                .andWhere { MessageTable.dateTime.between(startDateTime, endDateTime) }
                .map { it.toMessage() }
        }

    fun updateReactionCount(chatId: Long, messageId: Int, count: Int) = transaction {
        MessageTable
            .update({ (MessageTable.chatId eq chatId).and(MessageTable.messageId eq messageId) }) {
                with(SqlExpressionBuilder) { it[reactionCount] = reactionCount + count }
            }
    }

    fun getTopLiked(chatId: Long, top: Int) = transaction {
        MessageTable
            .select { MessageTable.chatId eq chatId }
            .andWhere { MessageTable.reactionCount greater 0 }
            .orderBy(MessageTable.reactionCount, SortOrder.DESC)
            .limit(top)
            .map { it.toMessage() }
    }
}