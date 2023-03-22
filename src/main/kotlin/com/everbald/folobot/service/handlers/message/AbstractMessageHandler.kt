package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.service.handlers.Handler
import mu.KLogging
import org.telegram.telegrambots.meta.api.objects.Update

abstract class AbstractMessageHandler : Handler, KLogging() {
    override fun canHandle(update: Update) = update.hasMessage()
}