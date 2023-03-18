package com.telegram.folobot.extensions

import com.telegram.folobot.model.ActionsEnum
import mu.KLogger
import org.telegram.telegrambots.meta.api.objects.Message

fun KLogger.addMessage(message: Message?) =
    message?.let { this.debug { "Replied to ${getChatIdentity(it.chatId)} with ${it.text ?: "pic"}" } }

fun KLogger.addMessageForward(message: Message?) =
    message?.let { this.info { "Forwarded message to ${getChatIdentity(it.chatId)}" } }

fun KLogger.addActionReceived(action: ActionsEnum, chatId: Long) =
    this.info { "Received request with action $action in chat ${getChatIdentity(chatId)}" }
