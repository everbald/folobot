package com.everbald.folobot.extensions

import org.telegram.telegrambots.meta.api.objects.Update

fun Update.getMsg() = this.message ?: this.callbackQuery?.message ?: throw RuntimeException("No message found in update")
fun Update.getFrom() = this.message?.from ?: this.callbackQuery?.from ?: throw RuntimeException("No user found in update")
fun Update.getChatId() = this.message?.chatId ?: this.callbackQuery?.message?.chatId ?: throw RuntimeException("No chatId found in update")