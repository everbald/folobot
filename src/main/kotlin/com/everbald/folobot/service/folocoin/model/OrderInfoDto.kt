package com.everbald.folobot.service.folocoin.model

import com.everbald.folobot.persistence.entity.OrderInfoEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment
import java.io.Serializable

val objectMapper: ObjectMapper
    get() {
        return jacksonObjectMapper()
    }

class OrderInfoDto(
    val id: Int? = null,
    var status: OrderStatus,
    val payment: SuccessfulPayment,
    val payload: InvoicePayload = objectMapper.readValue(payment.invoicePayload)
) : Serializable {
    fun setStatus(status: OrderStatus): OrderInfoDto {
        this.status = status
        return this
    }
}

fun OrderInfoDto.toEntity() = OrderInfoEntity(id, status, payment)