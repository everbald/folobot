package com.everbald.folobot.extensions

import org.telegram.telegrambots.meta.api.objects.Update

fun Update.getMessageText() = this.message?.text ?: this.callbackQuery?.message?.text
fun Update.getMessageCaption() = this.message?.caption ?: this.callbackQuery?.message?.caption
fun Update.getMessageReplyMarkup() = this.message?.replyMarkup ?: this.callbackQuery?.message?.replyMarkup