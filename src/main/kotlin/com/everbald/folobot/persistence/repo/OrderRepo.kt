package com.everbald.folobot.persistence.repo

import com.everbald.folobot.domain.OrderInfo
import com.everbald.folobot.persistence.mapper.toOrderInfo
import com.everbald.folobot.persistence.mapper.toOrderInfoUpsert
import com.everbald.folobot.persistence.table.OrderInfoTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

@Repository
class OrderRepo {
    fun save(orderInfo: OrderInfo): OrderInfo = transaction {
        OrderInfoTable
            .upsert { it.toOrderInfoUpsert(orderInfo) }
            .resultedValues
            ?.singleOrNull()
            ?.toOrderInfo()
            ?: throw RuntimeException("Couldn't save OrderInfo!")
    }

    fun find(id: Int): OrderInfo? = transaction {
        OrderInfoTable
            .select { OrderInfoTable.id eq id }
            .singleOrNull()
            ?.toOrderInfo()
    }

}