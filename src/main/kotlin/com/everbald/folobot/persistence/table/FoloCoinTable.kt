package com.everbald.folobot.persistence.table

import org.jetbrains.exposed.sql.Table

object FoloCoinTable : Table("folo_coin") {
    val userId = long("user_id")
    override val primaryKey = PrimaryKey(userId)
    val points = integer("points").default(0)
    val coins = integer("coins").default(0)
}