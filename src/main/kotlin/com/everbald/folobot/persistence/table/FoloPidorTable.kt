package com.everbald.folobot.persistence.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object FoloPidorTable : Table("folo_pidor") {
    val chatId = long("chat_id")
    val userId = reference("user_id", FoloUserTable.userId)
    override val primaryKey = PrimaryKey(chatId, userId)
    val score = integer("score")
    val lastWinDate = date("last_win_date")
}