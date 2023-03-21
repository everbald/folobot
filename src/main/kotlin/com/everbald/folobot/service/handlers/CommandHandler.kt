package com.everbald.folobot.service.handlers

import com.everbald.folobot.config.BotCredentialsConfig
import com.everbald.folobot.extensions.*
import com.everbald.folobot.model.Action
import com.everbald.folobot.model.BotCommand
import com.everbald.folobot.model.NumType
import com.everbald.folobot.service.*
import com.everbald.folobot.utils.FoloId.ANDREW_ID
import com.everbald.folobot.utils.Utils.Companion.getNumText
import com.everbald.folobot.utils.Utils.Companion.getPeriodText
import com.ibm.icu.text.RuleBasedNumberFormat
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@Component
@Priority(1)
class CommandHandler(
    private val foloVarService: FoloVarService,
    private val foloPidorService: FoloPidorService,
    private val messageService: MessageService,
    private val userService: UserService,
    private val textService: TextService,
    private val foloIndexChartService: FoloIndexChartService,
    private val smallTalkHandler: SmallTalkHandler,
    private val botCredentials: BotCredentialsConfig,
    private val inlineKeyboardService: InlineKeyboardService
) : Handler, KLogging() {
    fun Message.isMyCommand() =
        this.isCommand && this.isNotForward() &&
                (this.chat.isUserChat ||
                        this.entities.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text
                            ?.contains(botCredentials.botUsername) == true)

    override fun canHandle(update: Update): Boolean {
        return (update.hasMessage() && update.message.isMyCommand()).also {
            if (it) logger.addActionReceived(Action.COMMAND, update.message.chatId)
        }
    }

    override fun handle(update: Update) {
        when (
            BotCommand.fromCommand(update.message.getBotCommand()).also {
                logger.info { "Received command ${it ?: "UNDEFINED"} in chat ${getChatIdentity(update.message.chatId)}" }
            }
        ) {
            BotCommand.START -> messageService.sendSticker(messageService.randomSticker, update)
                .also { logger.info { "Sent sticker to ${getChatIdentity(update.message.chatId)}" } }

            BotCommand.SILENTSTREAM -> messageService.sendSticker(messageService.randomSticker, update)
                .also { logger.info { "Sent sticker to ${getChatIdentity(update.message.chatId)}" } }

            BotCommand.SMALLTALK -> smallTalk(update)
            BotCommand.FREELANCE -> frelanceTimer(update)
            BotCommand.NOFAP -> nofapTimer(update)
            BotCommand.FOLOPIDOR -> foloPidor(update)
            BotCommand.FOLOPIDORTOP -> foloPidorTop(update)
            BotCommand.FOLOSLACKERS -> foloSlackers(update)
            BotCommand.FOLOUNDERDOGS -> foloUnderdogs(update)
            BotCommand.FOLOPIDORALPHA -> alphaTimer(update)
            BotCommand.FOLOCOIN -> foloCoin(update)
            BotCommand.FOLOINDEXDYNAMICS -> foloIndexDinamics(update)
            else -> {}
        }
    }

    private fun smallTalk(update: Update) = smallTalkHandler.handle(update, withInit = true)

    /**
     * Подсчет времени прошедшего с дня F
     *
     * @param update [Update]
     */
    fun frelanceTimer(update: Update) {
        messageService.sendMessage(
            """
                18 ноября 2019 года я уволился с завода по своему желанию.
                С тех пор я стремительно вхожу в IT вот уже
                *${getPeriodText(Period.between(LocalDate.of(2019, 11, 18), LocalDate.now()))}*!
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
        if (update.message.from.isFo()) {
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
                "Для особо озабоченных в *" +
                        RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT)
                            .format(noFapCount.toLong(), "%spellout-ordinal-masculine") +
                        "* раз повторяю тут Вам, что я с *" +
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(Locale("ru"))
                            .format(noFapDate) +
                        "* и до сих пор вот уже *" +
                        getPeriodText(
                            Period.between(
                                noFapDate,
                                LocalDate.now()
                            )
                        ) +
                        "* твёрдо и уверенно держу \"Но Фап\".",
                update
            )
        }.also { logger.addMessage(it) }
    }

    /**
     * Определяет фолопидора дня. Если уже определен показывает кто
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun foloPidor(update: Update) {
        val chatId = update.message.chatId
        if (!update.message.isUserMessage) {
            //Определяем дату и победителя предыдущего запуска
            val lastDate = foloVarService.getLastFolopidorDate(chatId)
            val lastWinner = foloVarService.getLastFolopidorWinner(chatId)

            //Определяем либо показываем победителя
            if (lastWinner == FoloVarService.INITIAL_USERID || lastDate < LocalDate.now()) {
                //Выбираем случайного
                val foloPidor = foloPidorService.getRandom(chatId)

                //Обновляем счетчик
                foloPidor.score++
                foloPidor.lastWinDate = LocalDate.now()
                foloPidorService.save(foloPidor)
                logger.debug { "Updated $foloPidor score" }

                //Обновляем текущего победителя
                foloVarService.setLastFolopidorWinner(chatId, foloPidor.id.userId)
                foloVarService.setLastFolopidorDate(chatId, LocalDate.now())
                logger.info { "Updated foloPidor winner ${foloPidor.foloUser.getTagName()} and win date ${LocalDate.now()}" }

                //Поздравляем
                messageService.sendMessage(textService.setup, update)
                messageService.sendMessage(
                    textService.getPunch(userService.getFoloUserNameLinked(foloPidor, chatId)), update
                ).also { logger.addMessage(it) }

            } else {
                messageService.sendMessage(
                    "Фолопидор дня уже выбран, это *" +
                            userService.getFoloUserName(
                                foloPidorService.findById(chatId, lastWinner),
                                chatId
                            ) +
                            "*. Пойду лучше лампово попержу в диван",
                    update
                ).also { logger.addMessage(it) }
            }
        } else {
            messageService.sendMessage(
                text = "Для меня вы все фолопидоры, " +
                        userService.getFoloUserName(update.message.from),
                update = update,
                reply = true
            ).also { logger.addMessage(it) }
        }
    }

    /**
     * Показывает топ фолопидоров
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun foloPidorTop(update: Update) {
        if (!update.message.isUserMessage) {
            val top = StringJoiner("\n").add("Топ 10 *фолопидоров*:\n")
            val foloPidors = foloPidorService.getTop(update.message.chatId)
            for (i in foloPidors.indices) {
                val place = when (i) {
                    0 -> "\uD83E\uDD47"
                    1 -> "\uD83E\uDD48"
                    2 -> "\uD83E\uDD49"
                    else -> "\u2004*" + (i + 1) + "*.\u2004"
                }
                val foloPidor = foloPidors[i]
                top.add(
                    place + userService.getFoloUserName(foloPidor, update.message.chatId) + " — _" +
                            getNumText(foloPidor.score, NumType.COUNT) + "_"
                )
            }
            messageService.sendMessage(top.toString(), update)
        } else {
            messageService.sendMessage("Андрей - почетный фолопидор на все времена!", update)
        }.also { logger.addMessage(it) }
    }

    /**
     * Показывает топ фолослакеров
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun foloSlackers(update: Update) {
        if (!update.message.isUserMessage) {
            messageService.sendMessage(
                foloPidorService.getSlackers(update.message.chatId).withIndex().joinToString(
                    separator = "\n",
                    prefix = "*Фолопидоры не уделяющих фоломании достаточно времени*:\n\n",
                    transform = {
                        "\u2004*${it.index + 1}*.\u2004${
                            userService.getFoloUserName(it.value, update.message.chatId)
                        } — бездельничает _${getNumText(it.value.getPassiveDays(), NumType.DAY)}_"
                    }
                ),
                update
            )
        } else {
            messageService.sendMessage("Предавайтесь фоломании хотя бы 10 минут в день!", update)
        }.also { logger.addMessage(it) }
    }

    fun foloUnderdogs(update: Update) {
        if (!update.message.isUserMessage) {
            val foloUnderdogs = foloPidorService.getUnderdogs(update.message.chatId)
            if (foloUnderdogs.isNotEmpty()) {
                messageService.sendMessage(
                    text = "Когда-нибудь и вы станете *фолопидорами дня*, уважаемые фанаты " +
                            "и милые фанаточки, просто берите пример с Андрея!\n\n" +
                            foloUnderdogs.joinToString(
                                separator = "\n• ",
                                prefix = "• ",
                                transform = { foloPidor ->
                                    userService.getFoloUserName(foloPidor, update.message.chatId)
                                }
                            ),
                    update = update
                )
            } else {
                messageService.sendMessage(
                    text = "Все *фолопидоры* хотя бы раз побывали *фолопидорами дня*, это потрясающе!",
                    update = update
                )
            }
        } else {
            messageService.sendMessage(
                "Для меня вы все фолопидоры, " +
                        userService.getFoloUserName(update.message.from),
                update,
                reply = true
            )
        }.also { logger.addMessage(it) }
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
                        "*${
                            getNumText(
                                Period.between(alfaBirthday, nextAlphaBirthday).years,
                                NumType.YEARISH
                            )
                        }*!",
                update
            )
        } else {
            messageService.sendMessage(
                "День рождения моего хорошего друга и главного фолопидора " +
                        "[Андрея](tg://user?id=$ANDREW_ID) *" +
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(Locale("ru"))
                            .format(alfaBirthday) +
                        "* через *${getPeriodText(Period.between(LocalDate.now(), nextAlphaBirthday))}*",
                update
            )
        }.also { logger.addMessage(it) }
    }

    fun foloCoin(update: Update) =
        messageService.sendPhoto(
            "/static/images/foloStock.jpg",
            update.message.chatId,
            "Добро пожаловать на фолобиржу!",
            inlineKeyboardService.getFoloCoinKeyboard()
        )

    fun foloIndexDinamics(update: Update) {
        if (update.message.chat.isFolochat()) {
            val endDate = LocalDate.now().minusDays(1)
            val chart = foloIndexChartService.buildChart(
                update.message.chatId,
                endDate.minusMonths(1),
                endDate
            )
            messageService.sendPhoto(chart, update.message.chatId, "#динамикафолоиндекса")
                .also { logger.info { "Replied to ${getChatIdentity(update.message.chatId)} with IndexChart" } }
        } else {
            messageService.sendMessage("Фолоиндекс только для фолочата!", update)
        }.also { logger.addMessage(it) }
    }
}