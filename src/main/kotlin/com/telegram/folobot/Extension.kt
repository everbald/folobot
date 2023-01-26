package com.telegram.folobot

import org.telegram.telegrambots.meta.api.objects.Message

fun Message.isForward(): Boolean {
    return this.forwardDate != null
}
fun Message.isNotForward(): Boolean {
    return !this.isForward()
}
fun Message.isUserJoin(): Boolean {
    return this.newChatMembers.isNotEmpty()
}

fun Message.isNotUserJoin(): Boolean {
    return !this.isUserJoin()
}