package com.everbald.folobot.persistence.mapper

import com.everbald.folobot.domain.OrderInfo
import com.everbald.folobot.persistence.table.OrderInfoTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpsertStatement

fun UpsertStatement<Long>.toOrderInfoUpsert(orderInfo: OrderInfo) =
    this.apply {
        this[OrderInfoTable.userId] = orderInfo.userId
        this[OrderInfoTable.status] = orderInfo.status
        this[OrderInfoTable.payment] = orderInfo.payment
    }

fun ResultRow.toOrderInfo(): OrderInfo = OrderInfo(
    userId = this[OrderInfoTable.userId],
    status = this[OrderInfoTable.status],
    payment = this[OrderInfoTable.payment]
)