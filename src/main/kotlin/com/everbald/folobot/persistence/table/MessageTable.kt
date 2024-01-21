package com.everbald.folobot.persistence.table

import com.everbald.folobot.persistence.jsonb
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import org.telegram.telegrambots.meta.api.objects.Message

object MessageTable : Table("message") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val chatId = long("chat_id")
    val messageId = integer("message_id")
    val dateTime = timestampWithTimeZone("date_time")
    val message = jsonb<Message>("message")
    val reactionCount = integer("reaction_count")
}
