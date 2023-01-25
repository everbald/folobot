package com.telegram.folobot

import org.telegram.telegrambots.meta.api.objects.Message

fun Message.isForward(): Boolean {
    return this.forwardFrom != null || this.forwardSenderName != null || this.isAutomaticForward != null
}

fun Message.isNotForward(): Boolean {
    return !this.isForward()
}