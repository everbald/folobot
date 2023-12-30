package com.everbald.folobot.persistence.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object FoloIndexTable : Table("folo_index") {
    val chatId = long("chat_id")
    val date = date("date")
    override val primaryKey = PrimaryKey(chatId, date)
    val points = integer("points").default(0)
    val index = double("index").nullable()
}