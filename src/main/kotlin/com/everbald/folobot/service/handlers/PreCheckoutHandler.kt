package com.everbald.folobot.service.handlers

import com.everbald.folobot.extensions.*
import com.everbald.folobot.service.folocoin.PreCheckoutService
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(1)
class PreCheckoutHandler(
    private val preCheckoutService: PreCheckoutService,
) : Handler, KLogging() {
    override fun canHandle(update: Update): Boolean {
        return update.hasPreCheckoutQuery().also {
            if (it) logger.addPreCheckoutQueryReceived(update.preCheckoutQuery.from.getName())
        }
    }

    override fun handle(update: Update) {
        preCheckoutService.confirmOrder(update)
    }
}