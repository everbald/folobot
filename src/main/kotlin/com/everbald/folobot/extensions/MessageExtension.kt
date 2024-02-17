package com.everbald.folobot.extensions

import com.everbald.folobot.domain.type.BotCommand
import com.everbald.folobot.utils.FoloId.FOLOMKIN_ID
import com.everbald.folobot.utils.FoloId.FOLO_SWARM
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message

val Message.isForward: Boolean get() = this.forwardDate != null
val Message.isNotForward: Boolean get() = !this.isForward
val Message.isUserJoin: Boolean get() = this.newChatMembers.isNotEmpty()
val Message.isNotUserJoin: Boolean get() = !this.isUserJoin
val Message.isUserLeft: Boolean get() = this.leftChatMember != null
val Message.isSuccessfulPayment: Boolean get() = this.successfulPayment != null
val Message.isNotSuccessfulPayment: Boolean get() = !this.isSuccessfulPayment
val Message.isUserShared: Boolean get() = this.userShared != null
val Message.isNotUserShared: Boolean get() = !this.isUserShared
val Message.isTextMessage: Boolean get() = this.hasText() || this.caption != null
val Message.isNotCommand get() = !this.isCommand
fun Message?.isFromFoloSwarm() =
    FOLO_SWARM.contains(this?.forwardFromChat?.id) || this?.forwardFrom?.id == FOLOMKIN_ID

fun Message.extractText(): String? = this.text ?: this.caption

val Message?.isAboutFo: Boolean get() =
    this?.replyToMessage?.from?.id == FOLOMKIN_ID ||
            this?.entities?.any { it.type == EntityType.TEXTMENTION && it.user.isFo } == true ||
            listOf(
                "фоло", "фолик", "алекс", "гуру", "саш", "санчоус", "шурк", "гурманыч", "вайтифас", "просвещения",
                "цветочкин", "расческин", "folo", "яхтсмен"
            ).any {
                this?.text?.contains(it, true) == true ||
                        this?.caption?.contains(it, true) == true
            }


val Message?.isAboutBot: Boolean get() = listOf("гурманыч", "шурка").any {
    this?.text?.contains(it, true) == true ||
            this?.caption?.contains(it, true) == true
}

fun Message?.getBotCommand(): String? {
    var command = this?.entities?.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text?.substringBefore("@")
    if (command == BotCommand.START.command) {
        command = this?.text?.substringAfter(BotCommand.START.command)?.trimIndent().orEmpty()
            .ifEmpty { BotCommand.START.command }
    }
    return command
}

fun Message?.extractCommandText(): String? =
    this?.entities
        ?.firstOrNull { it.type == EntityType.BOTCOMMAND }
        ?.text
        ?.let { this.text?.replace(it, "") }
        ?.trimIndent()
        ?: this?.text






