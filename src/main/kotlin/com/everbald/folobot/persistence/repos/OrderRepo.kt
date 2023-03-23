package com.everbald.folobot.persistence.repos

import com.everbald.folobot.persistence.entity.OrderInfoEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepo : CrudRepository<OrderInfoEntity, Int> {
    fun findOrderById(id: Int): OrderInfoEntity?
}