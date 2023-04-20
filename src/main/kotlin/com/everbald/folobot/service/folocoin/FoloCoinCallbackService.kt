package com.everbald.folobot.service.folocoin

import com.everbald.folobot.extensions.format
import com.everbald.folobot.extensions.getChatIdentity
import com.everbald.folobot.extensions.getPremiumPrefix
import com.everbald.folobot.extensions.toText
import com.everbald.folobot.model.PluralType
import com.everbald.folobot.service.KeyboardService
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
import com.everbald.folobot.service.folocoin.sale.InvoiceService
import com.everbald.folobot.service.handlers.message.CommandHandler
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate

@Component
class FoloCoinCallbackService(
    private val foloCoinService: FoloCoinService,
    private val messageService: MessageService,
    private val userService: UserService,
    private val keyboardService: KeyboardService,
    private val invoiceService: InvoiceService,
    private val commandHandler: CommandHandler
) : KLogging() {

    fun coinBalance(update: Update) {
        val balance = foloCoinService.getById(update.callbackQuery.from.id).coins
        val text = if (balance > 0) {
            "На твоем счете *${balance.toText(PluralType.COIN)}*, уважаемый " +
                    "${update.callbackQuery.from.getPremiumPrefix()}фолопидор " +
                    userService.getFoloUserNameLinked(update.callbackQuery.from)
        } else {
            "На твоем счете нет фолокойнов, уважаемый ${update.callbackQuery.from.getPremiumPrefix()}фолопидор " +
                    userService.getFoloUserNameLinked(update.callbackQuery.from)
        }
        messageService.editMessageCaption(
            text,
            update,
            keyboardService.getFoloCoinKeyboard(update.callbackQuery.message.isUserMessage)
        )
            .also { logger.debug { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)} with coin balance" } }
    }

    fun coinPrice(update: Update) {
        val price = foloCoinService.getPrice()
        messageService.editMessageCaption(
            "Стоимость фолокойна на сегодня составляет *${price.format()}*₽",
            update,
            keyboardService.getFoloCoinKeyboard(update.callbackQuery.message.isUserMessage)
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
            keyboardService.getFoloCoinKeyboard(update.callbackQuery.message.isUserMessage)
        ).also { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)} with folomillionaire chart" }
    }

    fun buyCoin(update: Update) {
        messageService.editMessageCaption(
            "Создан счет на оплату",
            update,
            keyboardService.getFoloCoinKeyboard(update.callbackQuery.message.isUserMessage)
        ).also { logger.debug { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)}" +
                " with coin invoice" } }
        invoiceService.sendInvoice(update)
    }

    fun transferCoin(update: Update) {
        messageService.editMessageCaption(
            "Выбор фолопидора для перевода",
            update,
            keyboardService.getFoloCoinKeyboard(update.callbackQuery.message.isUserMessage)
        ).also { logger.debug { "Replied to ${getChatIdentity(update.callbackQuery.message.chatId)}" +
                " with folotransfer keyboard" } }
        commandHandler.foloCoinTransfer(update)
    }
}