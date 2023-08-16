package com.everbald.folobot.service.folocoin.model

import com.everbald.folobot.extensions.toObject
import com.everbald.folobot.persistence.entity.OrderInfoEntity
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment

class OrderInfoDto(
    val id: Int? = null,
    val userId: Long,
    var status: OrderStatus,
    val payment: SuccessfulPayment,
    val payload: InvoicePayload = payment.invoicePayload.toObject()
) {
    fun setStatus(status: OrderStatus): OrderInfoDto = this.also { it.status = status }
}

fun OrderInfoDto.toEntity() = OrderInfoEntity(id, userId, status, payment)