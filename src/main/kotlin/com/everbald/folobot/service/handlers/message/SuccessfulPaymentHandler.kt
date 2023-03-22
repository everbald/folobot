package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.addSuccessfulPaymentReceived
import com.everbald.folobot.service.folocoin.SuccessfulPaymentService
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(1)
class SuccessfulPaymentHandler(
    private val successfulPaymentService: SuccessfulPaymentService
) : AbstractMessageHandler() {
    override fun canHandle(update: Update): Boolean {
        return (update.message?.successfulPayment != null).also { if (it) logger.addSuccessfulPaymentReceived() }
    }

    override fun handle(update: Update) {
        successfulPaymentService.processPayment(update)
    }
}