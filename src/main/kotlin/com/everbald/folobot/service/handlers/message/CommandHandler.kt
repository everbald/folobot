package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.config.BotCredentialsConfig
import com.everbald.folobot.extensions.*
import com.everbald.folobot.model.Action
import com.everbald.folobot.model.BotCommand
import com.everbald.folobot.model.PluralType
import com.everbald.folobot.service.*
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.FoloIndexChartService
import com.everbald.folobot.service.folocoin.FoloIndexService.Companion.FOLO_STOCK_IMAGE
import com.everbald.folobot.utils.FoloId.ANDREW_ID
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import java.time.Period
import java.util.*

@Component
@Priority(1)
class CommandHandler(
    private val foloVarService: FoloVarService,
    private val messageService: MessageService,
    private val foloIndexChartService: FoloIndexChartService,
    private val smallTalkHandler: SmallTalkHandler,
    private val botCredentials: BotCredentialsConfig,
    private val keyboardService: KeyboardService,
    private val foloCoinService: FoloCoinService
) : AbstractMessageHandler() {
    fun Message.isMyCommand() =
        this.isCommand && this.isNotForward() &&
                (this.chat.isUserChat ||
                        this.entities.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text
                            ?.contains(botCredentials.botUsername) == true)

    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isMyCommand())
            .also { if (it) logger.addActionReceived(Action.COMMAND, update.message.chatId) }

    override fun handle(update: Update) {
        when (
            BotCommand.fromCommand(update.message.getBotCommand()).also {
                logger.addCommandReceived(
                    it,
                    getChatIdentity(update.message.chatId),
                    update.from.getName()
                )
            }
        ) {
            BotCommand.START -> messageService.sendSticker(messageService.randomSticker, update)
                .also { logger.info { "Sent sticker to ${getChatIdentity(update.message.chatId)}" } }

            BotCommand.SILENTSTREAM -> messageService.sendSticker(messageService.randomSticker, update)
                .also { logger.info { "Sent sticker to ${getChatIdentity(update.message.chatId)}" } }

            BotCommand.SMALLTALK -> smallTalk(update)
            BotCommand.FREELANCE -> freelanceTimer(update)
            BotCommand.NOFAP -> nofapTimer(update)
            BotCommand.FOLOPIDOR -> foloPidor(update)
            BotCommand.FOLOPIDORALPHA -> alphaTimer(update)
            BotCommand.FOLOCOIN -> foloCoin(update)
            BotCommand.FOLOCOINTRANSFER -> foloCoinTransfer(update)
            else -> {}
        }
    }

    private fun smallTalk(update: Update) = smallTalkHandler.handle(update, withInit = true)
//    private fun smallTalk(update: Update) =
//        messageService.sendMessage("Адекватное общение временно(?) под санкциями", update)

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
                *${Period.between(LocalDate.of(2019, 11, 18), LocalDate.now()).toText()}*!
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
                "Для особо озабоченных в *${noFapCount.spellOut()}* раз повторяю тут Вам, что я с " +
                        "*${noFapDate.toText()}* и до сих пор вот уже *${Period.between(noFapDate, LocalDate.now()).toText()}* " +
                        "твёрдо и уверенно держу \"Но Фап\".",
                update
            )
        }.also { logger.addMessage(it) }
    }

    fun foloPidor(update: Update) =
        messageService.sendPhoto(
            "/static/images/foloPidors.jpg",
            update.message.chatId,
            """
                МС Фоломкин создал новую религию,
                Миллионы фанатиков во всём мире слушают мои трэки,
                Для вас тексты моих песен священны, будто слово Божье в Библии
            """.trimIndent(),
            keyboardService.getFoloPidorKeyboard()
        )

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
                        "*${Period.between(alfaBirthday, nextAlphaBirthday).years.toText(PluralType.YEARISH)}*!",
                update
            )
        } else {
            messageService.sendMessage(
                "День рождения моего хорошего друга и главного фолопидора " +
                        "[Андрея](tg://user?id=$ANDREW_ID) *${alfaBirthday.toText()}* через " +
                        "*${Period.between(LocalDate.now(), nextAlphaBirthday).toText()}*",
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

    fun foloCoinTransfer(update: Update) : Message? {
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
}