package com.everbald.folobot.service.folocoin

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class SuccessfulPaymentService(
) : KLogging() {
    fun processPayment(update: Update) {
        // TODO add folocoin
    }

}