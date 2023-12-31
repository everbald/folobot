package com.everbald.folobot.persistence.table

import org.jetbrains.exposed.sql.Table

object FoloUserTable : Table("folo_user") {
    val userId = long("user_id")
    override val primaryKey = PrimaryKey(userId)
    val mainId = long("main_id")
    val name = varchar("name", 255)
    val tag  = varchar("tag",255)
    val anchor = bool("anchor").default(false)
}