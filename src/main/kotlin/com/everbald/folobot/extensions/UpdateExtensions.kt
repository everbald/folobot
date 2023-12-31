package com.everbald.folobot.extensions

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

val Update.msg: Message
    get() = this.message
        ?: this.callbackQuery?.message
        ?: throw RuntimeException("No message found in update")

val Update.chat: Chat? get() = this.msg.chat
val Update.chatId: Long get() = this.msg.chatId
val Update.from: User
    get() = this.message?.from
        ?: this.callbackQuery?.from
        ?: throw RuntimeException("No user found in update")
val Update.isUserMessage get() = this.msg.isUserMessage
val Update.isNotUserMessage get() = !this.isUserMessage
val Update.messageId: Int get() = this.msg.messageId