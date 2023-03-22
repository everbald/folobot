package com.everbald.folobot.service.folocoin.model

data class InvoicePayload(
    val product: Product,
    val userId: Long,
    val chatId: Long
)
