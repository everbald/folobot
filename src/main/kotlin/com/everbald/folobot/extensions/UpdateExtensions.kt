package com.everbald.folobot.extensions

import org.telegram.telegrambots.meta.api.objects.Update

fun Update.getMsg() = this.message ?: this.callbackQuery?.message ?: throw RuntimeException("No message found in update")