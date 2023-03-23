package com.everbald.folobot.extensions

import com.everbald.folobot.model.Action
import mu.KLogger
import org.telegram.telegrambots.meta.api.objects.Message

fun KLogger.addMessage(message: Message?) =
    message?.let { this.debug { "Replied to ${getChatIdentity(it.chatId)} with ${it.text ?: "pic"}" } }

fun KLogger.addMessageForward(message: Message?) =
    message?.let { this.info { "Forwarded message to ${getChatIdentity(it.chatId)}" } }

fun KLogger.addActionReceived(action: Action, chatId: Long) =
    this.info { "Received request with action $action in chat ${getChatIdentity(chatId)}" }

fun KLogger.addPreCheckoutQueryReceived(userIdentity: String) =
    this.info { "Received preCheckout query from $userIdentity" }

fun KLogger.addOutdatedInvoiceCheckout(userIdentity: String) =
    this.info { "Attempt to use an outdated invoice by $userIdentity" }

fun KLogger.addSuccessfulPaymentReceived(chatIdentity: String, userIdentity: String) =
    this.info { "Received successful payment from $userIdentity in chat $chatIdentity" }
