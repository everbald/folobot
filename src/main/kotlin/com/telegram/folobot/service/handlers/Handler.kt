package com.telegram.folobot.service.handlers

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

interface Handler {
    fun handle(update: Update) : BotApiMethod<*>?
}