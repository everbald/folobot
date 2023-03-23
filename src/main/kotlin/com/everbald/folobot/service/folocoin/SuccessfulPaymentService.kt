package com.everbald.folobot.service.folocoin

import com.everbald.folobot.persistence.repos.OrderRepo
import com.everbald.folobot.service.folocoin.model.OrderInfoDto
import com.everbald.folobot.service.folocoin.model.OrderStatus
import com.everbald.folobot.service.folocoin.model.toEntity
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class SuccessfulPaymentService(
    private val orderRepo: OrderRepo
) : KLogging() {
    fun processPayment(update: Update) {
        val newOrder = orderRepo.save(
            OrderInfoDto(
                status = OrderStatus.NEW,
                payment = update.message.successfulPayment
            ).toEntity()
        )
    }

}