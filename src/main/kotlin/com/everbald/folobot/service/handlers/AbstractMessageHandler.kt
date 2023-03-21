package com.everbald.folobot.service.handlers

import mu.KLogging
import org.telegram.telegrambots.meta.api.objects.Update

abstract class AbstractMessageHandler : Handler, KLogging() {
    override fun canHandle(update: Update) = update.hasMessage()
}