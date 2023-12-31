package com.everbald.folobot.domain

import com.everbald.folobot.extensions.toObject
import com.everbald.folobot.service.folocoin.model.InvoicePayload
import com.everbald.folobot.service.folocoin.model.OrderStatus
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment

class OrderInfo(
    val userId: Long,
    var status: OrderStatus,
    val payment: SuccessfulPayment,
    val payload: InvoicePayload = payment.invoicePayload.toObject()
) {
    fun setStatus(status: OrderStatus): OrderInfo = this.also { it.status = status }
}