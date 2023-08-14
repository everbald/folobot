package com.everbald.folobot.service.folocoin.model

import com.everbald.folobot.extensions.toObject
import com.everbald.folobot.persistence.entity.OrderInfoEntity
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment
import java.io.Serializable

class OrderInfoDto(
    val id: Int? = null,
    val userId: Long,
    var status: OrderStatus,
    val payment: SuccessfulPayment,
    val payload: InvoicePayload = payment.invoicePayload.toObject()
) : Serializable {
    fun setStatus(status: OrderStatus): OrderInfoDto {
        this.status = status
        return this
    }
}

fun OrderInfoDto.toEntity() = OrderInfoEntity(id, userId, status, payment)