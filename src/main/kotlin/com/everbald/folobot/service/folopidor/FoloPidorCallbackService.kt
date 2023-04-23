package com.everbald.folobot.service.folopidor

import com.everbald.folobot.extensions.*
import com.everbald.folobot.model.PluralType
import com.everbald.folobot.service.*
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import java.util.*

@Component
class FoloPidorCallbackService(
    private val messageService: MessageService,
    private val userService: UserService,
    private val foloVarService: FoloVarService,
    private val foloPidorService: FoloPidorService,
    private val textService: TextService,
    private val keyboardService: KeyboardService
) : KLogging() {
    fun foloPidor(update: Update) {
        if (!update.getMsg().isUserMessage) {
            //Определяем дату и победителя предыдущего запуска
            val lastDate = foloVarService.getLastFolopidorDate(update.getChatId())
            val lastWinner = foloVarService.getLastFolopidorWinner(update.getChatId())

            //Определяем либо показываем победителя
            if (lastWinner == FoloVarService.INITIAL_USERID || lastDate < LocalDate.now()) {
                //Выбираем случайного
                val foloPidor = foloPidorService.getRandom(update.getChatId())

                //Обновляем счетчик
                foloPidor.score++
                foloPidor.lastWinDate = LocalDate.now()
                foloPidorService.save(foloPidor)
                logger.debug { "Updated $foloPidor score" }

                //Обновляем текущего победителя
                foloVarService.setLastFolopidorWinner(update.getChatId(), foloPidor.id.userId)
                foloVarService.setLastFolopidorDate(update.getChatId(), LocalDate.now())
                logger.info { "Updated foloPidor winner ${foloPidor.foloUser.getTagName()} and win date ${LocalDate.now()}" }

                //Поздравляем
                messageService.editMessageCaption(
                    "Выбираем фолопидора дня...",
                    update,
                    keyboardService.getFoloPidorKeyboard()
                )
                messageService.sendMessage(textService.setup, update)
                messageService.sendMessage(
                    textService.getPunch(userService.getFoloUserNameLinked(foloPidor, update.getChatId())), update
                ).also { logger.addMessage(it) }
            } else {
                messageService.editMessageCaption(
                    "Фолопидор дня уже выбран, это *" +
                            userService.getFoloUserName(
                                foloPidorService.findById(update.getChatId(), lastWinner),
                                update.getChatId()
                            ) +
                            "*. Пойду лучше лампово попержу в диван",
                    update,
                    keyboardService.getFoloPidorKeyboard()
                ).also {
                    logger.debug {
                        "Replied to ${getChatIdentity(update.getChatId())} with folopidor of the day"
                    }
                }
            }
        } else {
            messageService.editMessageCaption(
                "Для меня вы все фолопидоры, ${userService.getFoloUserName(update.getFrom())}",
                update,
                keyboardService.getFoloPidorKeyboard()
            ).also {
                logger.debug {
                    "Replied to ${getChatIdentity(update.getChatId())} with folopidor of the day"
                }
            }
        }
    }

    fun foloPidorTop(update: Update) {
        val text = if (!update.getMsg().isUserMessage) {
            val top = StringJoiner("\n").add("Топ 10 *фолопидоров*:\n")
            val foloPidors = foloPidorService.getTop(update.getChatId())
            for (i in foloPidors.indices) {
                val place = when (i) {
                    0 -> "\uD83E\uDD47"
                    1 -> "\uD83E\uDD48"
                    2 -> "\uD83E\uDD49"
                    else -> "\u2004*" + (i + 1) + "*.\u2004"
                }
                val foloPidor = foloPidors[i]
                top.add(
                    place + userService.getFoloUserName(foloPidor, update.getChatId()) + " — _" +
                            foloPidor.score.toText(PluralType.COUNT) + "_"
                )
            }
            top.toString()
        } else {
            "Андрей - почетный фолопидор на все времена!"
        }
        messageService.editMessageCaption(text, update, keyboardService.getFoloPidorKeyboard()).also {
            logger.debug {
                "Replied to ${getChatIdentity(update.getChatId())} with folopidor top"
            }
        }
    }

    /**
     * Показывает топ фолослакеров
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun foloSlackers(update: Update) {
        val text = if (!update.getMsg().isUserMessage) {
            val slackers = foloPidorService.getSlackers(update.getChatId())
            if (slackers.isNotEmpty()) {
                foloPidorService.getSlackers(update.getChatId()).withIndex().joinToString(
                    separator = "\n",
                    prefix = "*Фолопидоры не уделяющих фоломании достаточно времени*:\n\n",
                    transform = {
                        "\u2004*${it.index + 1}*.\u2004${
                            userService.getFoloUserName(it.value, update.getChatId())
                        } — бездельничает _${it.value.getPassiveDays().toText(PluralType.DAY)}_"
                    }
                )
            } else {
                "Все *фолопидоры* были активны в последнее время! Но иногда можно и в диван попердеть..."
            }
        } else {
            "Предавайтесь фоломании хотя бы 10 минут в день!"
        }
        messageService.editMessageCaption(text, update, keyboardService.getFoloPidorKeyboard()).also {
            logger.debug {
                "Replied to ${getChatIdentity(update.getChatId())} with foloslackers"
            }
        }
    }

    fun foloUnderdogs(update: Update) {
        val text = if (!update.getMsg().isUserMessage) {
            val foloUnderdogs = foloPidorService.getUnderdogs(update.getChatId())
            if (foloUnderdogs.isNotEmpty()) {
                "Когда-нибудь и вы станете *фолопидорами дня*, уважаемые фанаты " +
                        "и милые фанаточки, просто берите пример с Андрея!\n\n" +
                        foloUnderdogs.joinToString(
                            separator = "\n• ",
                            prefix = "• ",
                            transform = { foloPidor ->
                                userService.getFoloUserName(foloPidor, update.getChatId())
                            }
                        )
            } else {
                "Все *фолопидоры* хотя бы раз побывали *фолопидорами дня*, это потрясающе!"
            }
        } else {
            "Для меня вы все фолопидоры, ${userService.getFoloUserName(update.getFrom())}"
        }
        messageService.editMessageCaption(text, update, keyboardService.getFoloPidorKeyboard()).also {
            logger.debug {
                "Replied to ${getChatIdentity(update.getChatId())} with folounderdogs"
            }
        }
    }

}
