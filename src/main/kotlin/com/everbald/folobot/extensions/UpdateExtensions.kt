package com.everbald.folobot.extensions

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

val Update.msg: Message
    get() = this.message
        ?: this.callbackQuery?.message as? Message
        ?: throw RuntimeException("No message found in update")

val Update.chat: Chat get() =
    this.message?.chat
        ?: (this.callbackQuery?.message as? Message)?.chat
        ?: this.messageReaction?.chat
        ?: throw RuntimeException("No chat found in update")

val Update.chatId: Long get() =
    this.message?.chatId
        ?: this.callbackQuery?.message?.chatId
        ?: this.messageReaction?.chat?.id
        ?: throw RuntimeException("No chatId found in update")

val Update.from: User
    get() = this.message?.from
        ?: this.callbackQuery?.from
        ?: throw RuntimeException("No user found in update")
val Update.isUserMessage get() = this.msg.isUserMessage
val Update.isNotUserMessage get() = !this.isUserMessage
val Update.messageId: Int get() =
    this.message?.messageId
        ?: (this.callbackQuery?.message as? Message)?.messageId
        ?: this.messageReaction?.messageId
        ?: throw RuntimeException("No messageId found in update")

fun Update.hasMessageReaction() = this.messageReaction != null