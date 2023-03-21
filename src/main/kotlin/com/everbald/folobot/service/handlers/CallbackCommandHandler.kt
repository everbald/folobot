package com.everbald.folobot.service.handlers

import com.everbald.folobot.extensions.*
import com.everbald.folobot.model.CallbackCommand
import com.everbald.folobot.model.NumType
import com.everbald.folobot.service.*
import com.everbald.folobot.utils.Utils.Companion.getNumText
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import java.util.*

@Component
@Priority(1)
class CallbackCommandHandler(
    private val foloCoinService: FoloCoinService,
    private val messageService: MessageService,
    private val userService: UserService,
    private val inlineKeyboardService: InlineKeyboardService
) : Handler, KLogging() {
    override fun canHandle(update: Update) = CallbackCommand.isMyCommand(update.callbackQuery?.data)
    override fun handle(update: Update) {
        when (
            CallbackCommand.fromCommand(update.callbackQuery.data).also {
                logger.info { "Received command ${it ?: "UNDEFINED"} in chat ${getChatIdentity(update.callbackQuery.message.chatId)}" }
            }
        ) {
            CallbackCommand.COINBALANCE -> coinBalance(update)
            CallbackCommand.COINPRICE -> coinPrice(update)
            CallbackCommand.FOLOMILLIONAIRE -> foloMillionaire(update)
            CallbackCommand.BUYCOIN -> buyCoin(update)
            else -> {}
        }
    }

    fun coinBalance(update: Update) {
        val balance = foloCoinService.getById(update.callbackQuery.from.id).coins
        val text = if (balance > 0) {
            "На твоем счете *${getNumText(balance, NumType.COIN)}*, уважаемый " +
                    "${update.callbackQuery.from.getPremiumPrefix()}фолопидор " +
                    userService.getFoloUserNameLinked(update.callbackQuery.from)
        } else {
            "На твоем счете нет фолокойнов, уважаемый ${update.callbackQuery.from.getPremiumPrefix()}фолопидор " +
                    userService.getFoloUserNameLinked(update.callbackQuery.from)
        }
        messageService.editMessageCaption(text, update, inlineKeyboardService.getFoloCoinKeyboard())
            .also { logger.debug { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)} with coin balance" } }
    }

    fun coinPrice(update: Update) {
        val price = foloCoinService.getPrice()
        messageService.editMessageCaption(
            "Стоимость фолокойна на сегодня составляет *${price.format()}*₽",
            update,
            inlineKeyboardService.getFoloCoinKeyboard()
        ).also { logger.debug { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)} with coin price" } }
    }

    fun foloMillionaire(update: Update) {
        messageService.editMessageCaption(
            foloCoinService.getTop().withIndex().joinToString(
                separator = "\n",
                prefix = "*10 богатейших фолопидоров мира, чье состояние исчисляется в фолокойнах " +
                        "— ${LocalDate.now().year}. Рейтинг Forbes*:\n",
                transform = {
                    "\u2004*${it.index + 1}*.\u2004${userService.getFoloUserName(it.value.userId)} — " +
                            "*₣${it.value.coins}*"
                }
            ),
            update,
            inlineKeyboardService.getFoloCoinKeyboard()
        ).also { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)} with folomillionaire chart" }
    }

    fun buyCoin(update: Update) {
        messageService.editMessageCaption(
            "Старт продаж ₣ ожидается в течении недели ",
            update,
            inlineKeyboardService.getFoloCoinKeyboard()
        ).also { logger.debug { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)} with coin invoice" } }
    }
}