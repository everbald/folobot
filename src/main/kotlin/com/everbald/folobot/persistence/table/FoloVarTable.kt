package com.everbald.folobot.persistence.table

import com.everbald.folobot.domain.type.VarType
import org.jetbrains.exposed.sql.Table

object FoloVarTable : Table("folo_var") {
    val chatId = long("chat_id")
    val type = enumerationByName<VarType>("type", 255)
    override val primaryKey = PrimaryKey(chatId, type)
    val value = varchar("value", 255).nullable()
}