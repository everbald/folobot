package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.addSuccessfulPaymentReceived
import com.everbald.folobot.extensions.getChatIdentity
import com.everbald.folobot.extensions.getName
import com.everbald.folobot.extensions.isSuccessfulPayment
import com.everbald.folobot.service.folocoin.sale.SuccessfulPaymentService
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(1)
class SuccessfulPaymentHandler(
    private val successfulPaymentService: SuccessfulPaymentService
) : AbstractMessageHandler() {
    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isSuccessfulPayment())
        .also {
            if (it) logger.addSuccessfulPaymentReceived(
                getChatIdentity(update.message.chatId),
                update.message.from.getName()
            )
        }


    override fun handle(update: Update) {
        successfulPaymentService.processPayment(update)
    }
}