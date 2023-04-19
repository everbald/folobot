package com.everbald.folobot.extensions

import com.everbald.folobot.model.BotCommand
import com.everbald.folobot.utils.FoloId.FOLOMKIN_ID
import com.everbald.folobot.utils.FoloId.FOLO_SWARM
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message

fun Message.isForward() = this.forwardDate != null
fun Message.isNotForward() = !this.isForward()
fun Message.isUserJoin() = this.newChatMembers.isNotEmpty()
fun Message.isNotUserJoin() = !this.isUserJoin()
fun Message.isUserLeft() = this.leftChatMember != null
fun Message.isSuccessfulPayment() = this.successfulPayment != null
fun Message.isNotSuccessfulPayment() = !this.isSuccessfulPayment()
fun Message?.isFromFoloSwarm() =
    FOLO_SWARM.contains(this?.forwardFromChat?.id) || this?.forwardFrom?.id == FOLOMKIN_ID

fun Message?.isAboutFo() =
    this?.replyToMessage?.from?.id == FOLOMKIN_ID ||
            this?.entities?.any { it.type == EntityType.TEXTMENTION && it.user.isFo() } == true ||
            listOf(
                "фоло", "фолик", "алекс", "гуру", "саш", "санчоус", "шурк", "гурманыч", "вайтифас", "просвещения",
                "цветочкин", "расческин", "folo"
            ).any {
                this?.text?.contains(it, true) == true ||
                        this?.caption?.contains(it, true) == true
            }


fun Message?.isAboutBot() = listOf("гурманыч", "шурка").any {
    this?.text?.contains(it, true) == true ||
            this?.caption?.contains(it, true) == true
}

fun Message?.getBotCommand(): String? {
    var command = this?.entities?.firstOrNull { it.type == "bot_command" }?.text?.substringBefore("@")
    if (command == BotCommand.START.command) {
        command = this?.text?.substringAfter(BotCommand.START.command)?.trimIndent().orEmpty()
            .ifEmpty { BotCommand.START.command }
    }
    return command
}


