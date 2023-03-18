package com.telegram.folobot.extensions

import mu.KLogger
import org.telegram.telegrambots.meta.api.objects.Message

fun KLogger.addToLog(message: Message?) =
    message?.let { this.debug { "Replied to ${getChatIdentity(it.chatId)} with ${it.text ?: "pic"}" } }