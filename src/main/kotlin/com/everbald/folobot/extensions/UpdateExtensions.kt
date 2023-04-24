package com.everbald.folobot.extensions

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Update

fun Update.getMsg() = this.message ?: this.callbackQuery?.message ?: throw RuntimeException("No message found in update")
val  Update.chat: Chat? get() = this.getMsg().chat
val Update.chatId: Long get() = this.getMsg().chatId
val Update.from get() = this.message?.from ?: this.callbackQuery?.from ?: throw RuntimeException("No user found in update")
val Update.isUserMessage get() = this.getMsg().isUserMessage

val Update.messageId: Int get() = this.getMsg().messageId