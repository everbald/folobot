package com.everbald.folobot.extensions

import com.everbald.folobot.domain.type.Action
import com.everbald.folobot.domain.type.BotCommand
import com.everbald.folobot.domain.type.CallbackCommand
import mu.KLogger
import org.telegram.telegrambots.meta.api.objects.Message

fun KLogger.addMessage(message: Message?) =
    message?.let { this.debug { "Replied to ${it.chatId.chatIdentity} with ${it.text ?: "pic"}" } }

fun KLogger.addMessageForward(message: Message?) =
    message?.let { this.info { "Forwarded message to ${it.chatId.chatIdentity}" } }

fun KLogger.addActionReceived(action: Action, chatId: Long) =
    this.info { "Received request with action $action in chat ${chatId.chatIdentity}" }

fun KLogger.addCallbackCommandReceived(callbackCommand: CallbackCommand?, chatIdentity: String, userIdentity: String) =
    this.info { "Received callback command ${callbackCommand ?: "UNDEFINED"} from $userIdentity in chat $chatIdentity" }

fun KLogger.addCommandReceived(botCommand: BotCommand?, chatIdentity: String, userIdentity: String) =
    this.info { "Received command ${botCommand ?: "UNDEFINED"} from $userIdentity in chat $chatIdentity" }

fun KLogger.addPreCheckoutQueryReceived(userIdentity: String) =
    this.info { "Received preCheckout query from $userIdentity" }

fun KLogger.addOutdatedInvoiceCheckout(userIdentity: String) =
    this.info { "Attempt to use an outdated invoice by $userIdentity" }

fun KLogger.addSuccessfulPaymentReceived(chatIdentity: String, userIdentity: String) =
    this.info { "Received successful payment from $userIdentity in chat $chatIdentity" }

fun KLogger.addUserSharedReceived(chatIdentity: String, userIdentity: String) =
    this.info { "Received user shared from $userIdentity in chat $chatIdentity" }
