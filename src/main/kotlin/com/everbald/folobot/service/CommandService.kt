package com.everbald.folobot.service

import com.everbald.folobot.domain.type.PluralType
import com.everbald.folobot.extensions.addMessage
import com.everbald.folobot.extensions.chatId
import com.everbald.folobot.extensions.chatIdentity
import com.everbald.folobot.extensions.from
import com.everbald.folobot.extensions.isFo
import com.everbald.folobot.extensions.isUserMessage
import com.everbald.folobot.extensions.spellOut
import com.everbald.folobot.extensions.toTextWithNumber
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.FoloIndexService.Companion.FOLO_STOCK_IMAGE
import com.everbald.folobot.service.hh.HHService
import com.everbald.folobot.utils.FoloId.ANDREW_ID
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import java.time.Period
import java.util.StringJoiner

@Component
@Priority(1)
class CommandService(
    private val foloPidorService: FoloPidorService,
    private val foloVarService: FoloVarService,
    private val messageService: MessageService,
    private val messageQueueService: MessageQueueService,
    private val keyboardService: KeyboardService,
    private val foloCoinService: FoloCoinService,
    private val foloBailService: FoloBailService,
    private val smallTalkService: SmallTalkService,
    private val hhService: HHService,
    private val userService: UserService,
) : KLogging() {
    /**
     * Подсчет времени прошедшего с дня F
     *
     * @param update [Update]
     */
    fun freelanceTimer(update: Update) {
        messageService.sendMessage(
            """
                18 ноября 2019 года я уволился с завода по своему желанию.
                С тех пор я стремительно вхожу в IT вот уже
                *${Period.between(LocalDate.of(2019, 11, 18), LocalDate.now()).toTextWithNumber()}*!
            """.trimIndent(),
            update
        ).also { logger.addMessage(it) }
    }

    /**
     * Подсчет времени прошедшего с последнего фапа. Фо обновляет таймер
     */
    fun nofapTimer(update: Update) {
        val noFapDate: LocalDate
        var noFapCount = 0
        // Фо устанавливает дату
        if (update.message.from.isFo) {
            noFapDate = LocalDate.now()
            foloVarService.setLastFapDate(noFapDate)
        } else {
            noFapDate = foloVarService.getLastFapDate()
            noFapCount = foloVarService.getNoFapCount(update.message.chatId)
        }
        if (noFapDate == LocalDate.now()) {
            messageService.sendMessage(
                """
                    Все эти молоденькие няшные студенточки вокруг...
                    Сорвался "Но Фап" сегодня...
                """.trimIndent(),
                update
            )
        } else {
            messageService.sendMessage(
                "Для особо озабоченных в *${noFapCount.spellOut()}* раз повторяю тут Вам, что я с " +
                        "*${noFapDate.toTextWithNumber()}* и до сих пор вот уже *${
                            Period.between(noFapDate, LocalDate.now()).toTextWithNumber()
                        }* " +
                        "твёрдо и уверенно держу \"Но Фап\".",
                update
            )
        }.also { logger.addMessage(it) }
    }

    fun foloPidorTop(update: Update) {
        (if (!update.isUserMessage) {
            val top = StringJoiner("\n").add("Топ 10 *фолопидоров*:\n")
            val foloPidors = foloPidorService.getTop(update.chatId)
            for (i in foloPidors.indices) {
                val place = when (i) {
                    0 -> "\uD83E\uDD47"
                    1 -> "\uD83E\uDD48"
                    2 -> "\uD83E\uDD49"
                    else -> "\u2004*" + (i + 1) + "*.\u2004"
                }
                val foloPidor = foloPidors[i]
                top.add(
                    place + userService.getFoloUserName(foloPidor, update.chatId) + " — _" +
                            foloPidor.score.toTextWithNumber(PluralType.COUNT) + "_"
                )
            }
            top.toString()
        } else "Андрей - почетный фолопидор на все времена!")
            .let { messageService.sendMessage(it, update) }
            .also { logger.debug { "Replied to ${update.chatId.chatIdentity} with folopidor top" } }
    }

    /**
     * Подсчет времени до дня рождения альфы
     *
     * @param update [Update]
     */
    fun alphaTimer(update: Update) {
        val alfaBirthday = LocalDate.of(1983, 8, 9)
        val alphaBirthdayThisYear = alfaBirthday.withYear(LocalDate.now().year)
        val nextAlphaBirthday =
            if (alphaBirthdayThisYear.isBefore(LocalDate.now()))
                alphaBirthdayThisYear.plusYears(1)
            else alphaBirthdayThisYear

        if (nextAlphaBirthday == LocalDate.now()) {
            messageService.sendMessage(
                "Поздравляю моего хорошего друга и главного фолопидора " +
                        "[Андрея](tg://user?id=$ANDREW_ID) с днем рождения!\nСегодня ему исполнилось " +
                        "*${Period.between(alfaBirthday, nextAlphaBirthday).years.toTextWithNumber(PluralType.YEARISH)}*!",
                update
            )
        } else {
            messageService.sendMessage(
                "День рождения моего хорошего друга и главного фолопидора " +
                        "[Андрея](tg://user?id=$ANDREW_ID) *${alfaBirthday.toTextWithNumber()}* через " +
                        "*${Period.between(LocalDate.now(), nextAlphaBirthday).toTextWithNumber()}*",
                update
            )
        }.also { logger.addMessage(it) }
    }

    fun foloCoin(update: Update) =
        messageService.sendPhoto(
            FOLO_STOCK_IMAGE,
            update.message.chatId,
            "Добро пожаловать на фолобиржу!",
            keyboardService.getFoloCoinKeyboard(update.message.isUserMessage)
        )

    fun foloCoinTransfer(update: Update): Message? {
        val coinBalance = foloCoinService.getById(update.from.id).coins
        return if (coinBalance > 0) {
            messageService.sendMessage(
                """
                    Выбери фолопидора для перевода.
                    Для удобства поиска его вероятно стоит добавить в контакты
                """.trimIndent(),
                update,
                keyboardService.getFolocoinTransferKeyboard()
            )
        } else {
            messageService.sendMessage(
                "На твоем счете нет фолокойнов досупных для трансфера дугому фолопидору 🫥",
                update,
            )
        }.also { logger.addMessage(it) }
    }

    fun aboutIt(update: Update) {
        hhService.getVacancy()
            ?.let {
                messageQueueService.sendAndAddToQueue(
                    "Ок, ${userService.getCustomNameLinked(update.from, update.chatId)}, " +
                            "вот что мне удалось найти по запросу \"вхождение в IT\": \n\n" + it,
                    update
                )
            } ?: let {
            messageQueueService.sendAndAddToQueue(
                "Ок, ${userService.getCustomNameLinked(update.from, update.chatId)}, " +
                        "по запросу \"вхождение в IT\" ничего не найдено",
                update
            )
        }
    }

    fun foloBail(update: Update) =
        messageService.sendPhoto(
            chatId = update.chatId,
            photoPath = "/static/images/skibidiBoba.jpg",
            text = foloBailService.buildTodayBailText(update.chatId)
        )

    fun createImage(update: Update) =
        smallTalkService.createImage(update)
}