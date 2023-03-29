package com.everbald.folobot.service.folocoin.model

data class InvoicePayload(
    val product: Product,
    val price: Double,
    val chatId: Long,
    val isPrivateChat: Boolean
)
