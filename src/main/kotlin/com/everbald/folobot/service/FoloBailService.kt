package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloMessage
import com.everbald.folobot.domain.type.PluralType
import com.everbald.folobot.extensions.toText
import com.everbald.folobot.extensions.toTextWithNumber
import com.everbald.folobot.persistence.repo.MessageRepo
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

@Service
class FoloBailService(
    private val repo: MessageRepo,
    private val userService: UserService,
) {
    fun getTodayBails(chatId: Long): List<FoloMessage> =
        repo.getInInterval(chatId, LocalDate.now().toOffsetAtStartOfDay(), LocalDate.now().toOffsetAtEndOfDay())
            .filter { it.message.text.isBail() }

    fun buildTodayBailText(chatId: Long, fullList: Boolean = true) =
        getTodayBails(chatId)
            .sortedByDescending { it.message.isReply }
            .let { bails ->
                if (bails.isNotEmpty()) {
                    "*Сегодня ${bails.size.toText(PluralType.BAIL_COUNTED)} " +
                            "${bails.size.toTextWithNumber(PluralType.BAIL)}*" +
                            if (fullList) "*:*\n${bails.buildBailText()}" else ""
                }
                else "*Сегодня без сливов, но не стоит расслабляться!*"
            }

    private fun String?.isBail(): Boolean =
        this?.let { it.contains("слив", true) && it.contains("засчит", true) }
            ?: false

    private fun List<FoloMessage>.buildBailText(): String =
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