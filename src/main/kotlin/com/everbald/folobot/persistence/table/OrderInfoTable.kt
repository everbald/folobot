package com.everbald.folobot.persistence.table


import com.everbald.folobot.persistence.jsonb
import com.everbald.folobot.service.folocoin.model.OrderStatus
import org.jetbrains.exposed.sql.Table
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment

object OrderInfoTable : Table("order_info") {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val userId = reference("user_id", FoloUserTable.userId)
    val status = enumerationByName<OrderStatus>("status", 64)
    val payment = jsonb<SuccessfulPayment>("payment")
}