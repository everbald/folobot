package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloBail
import com.everbald.folobot.domain.type.PluralType
import com.everbald.folobot.extensions.chatId
import com.everbald.folobot.extensions.isNotUserMessage
import com.everbald.folobot.extensions.msg
import com.everbald.folobot.extensions.toText
import com.everbald.folobot.mapper.toFoloBail
import com.everbald.folobot.persistence.repo.FoloBailRepo
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

@Service
class FoloBailService(
    private val repo: FoloBailRepo,
    private val userService: UserService,
    private val messageService: MessageService
) {
    fun register(update: Update) {
        if (update.isNotUserMessage && update.msg.text.isBail()) {
            update.msg.toFoloBail()
                .let { repo.save(it) }
        }
    }

    fun getTodayBails(chatId: Long) =
        repo.getInInterval(chatId, LocalDate.now().toOffsetAtStartOfDay(), LocalDate.now().toOffsetAtEndOfDay())

    fun sendTodayBails(update: Update) {
        getTodayBails(update.chatId)
            .sortedByDescending { it.message.isReply }
            .let { bails ->
                messageService.sendPhoto(
                    chatId = update.chatId,
                    photoPath = "/static/images/skibidiBoba.jpg",
                    text =
                    if (bails.isNotEmpty())
                        "*Сегодня зафиксировано ${bails.size.toText(PluralType.BAIL)}:* \n${bails.buildBailText()}"
                    else "*Сегодня без сливов, но не стоит расслабляться!*"
                )
            }
    }

    private fun String?.isBail(): Boolean =
        this?.let { it.contains("слив", true) && it.contains("засчит", true) }
            ?: false

    private fun List<FoloBail>.buildBailText(): String =
        this.withIndex()
            .joinToString(
                separator = "\n",
                prefix = "\n",
                transform = {
                    "${it.index + 1}. ${userService.getFoloUserNameLinked(it.value.message.from)} отметил " +
                            when (it.value.message.isReply) {
                                true -> "[слив](t.me/${it.value.message.chat.userName}/${it.value.message.messageId}) фолопидора " +
                                        userService.getFoloUserNameLinked(it.value.message.replyToMessage.from)

                                false -> "неизвестный [слив](t.me/${it.value.message.chat.userName}/${it.value.message.messageId})"
                            }
                }
            )
}

fun LocalDate.toOffsetAtStartOfDay(): OffsetDateTime =
    this.atStartOfDay(ZoneId.of("Europe/Moscow")).toOffsetDateTime()

fun LocalDate.toOffsetAtEndOfDay(): OffsetDateTime =
    this.plusDays(1).atStartOfDay(ZoneId.of("Europe/Moscow")).toOffsetDateTime().minusNanos(1)