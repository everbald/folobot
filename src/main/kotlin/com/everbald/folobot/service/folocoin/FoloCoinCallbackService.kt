package com.everbald.folobot.service.folocoin

import com.everbald.folobot.extensions.*
import com.everbald.folobot.domain.type.PluralType
import com.everbald.folobot.service.CommandService
import com.everbald.folobot.service.KeyboardService
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
import com.everbald.folobot.service.folocoin.FoloIndexService.Companion.FOLO_STOCK_IMAGE
import com.everbald.folobot.service.folocoin.sale.InvoiceService
import com.everbald.folobot.utils.FoloId
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate

@Component
class FoloCoinCallbackService(
    private val foloCoinService: FoloCoinService,
    private val messageService: MessageService,
    private val userService: UserService,
    private val keyboardService: KeyboardService,
    private val invoiceService: InvoiceService,
    private val commandService: CommandService,
    private val foloIndexChartService: FoloIndexChartService
) : KLogging() {

    fun coinBalance(update: Update) {
        val balance = foloCoinService.getById(update.from.id).coins
        val text = if (balance > 0) {
            "Ты намайнил *${balance.toTextWithNumber(PluralType.PRETOKEN)}*, " +
                    userService.getCustomName(update.from, update.chatId)
        } else {
            "Ты не майнил претокены, " +
                    userService.getCustomName(update.from, update.chatId)
        }
        messageService.editMessagePhoto(
            buildTitlePhotoFile(),
            text,
            update,
            keyboardService.getFoloCoinKeyboard(update.isUserMessage)
        ).also { logger.debug { "Replied to ${update.chatId.chatIdentity} with coin balance" } }
    }

    fun coinPrice(update: Update) {
        foloCoinService.getPrice()
            .let { price ->
                messageService.editMessagePhoto(
                    buildTitlePhotoFile(),
                    "Стоимость фолокойна на сегодня составляет *${price.format()}*₽",
                    update,
                    keyboardService.getFoloCoinKeyboard(update.isUserMessage)
                )
            }.also { logger.debug { "Replied to ${update.chatId.chatIdentity} with coin price" } }
    }

    fun foloMillionaire(update: Update) {
        messageService.editMessagePhoto(
            buildTitlePhotoFile(),
            foloCoinService.getTop().withIndex().joinToString(
                separator = "\n",
                prefix = "*10 фолопидоров, намайнивших больше всего претокенов*:\n",
                transform = {
                    "\u2004*${it.index + 1}*.\u2004${userService.getFoloUserName(it.value.userId)} — " +
                            "*₣${it.value.coins}*"
                }
            ),
            update,
            keyboardService.getFoloCoinKeyboard(update.isUserMessage)
        ).also { "Replied to ${update.chatId.chatIdentity} with folomillionaire chart" }
    }

    fun buyCoin(update: Update) {
        messageService.editMessagePhoto(
            buildTitlePhotoFile(),
            "Создан счет на оплату",
            update,
            keyboardService.getFoloCoinKeyboard(update.isUserMessage)
        ).also { logger.debug { "Replied to ${update.chatId.chatIdentity} with coin invoice" } }
        invoiceService.sendInvoice(update)
    }

    fun transferCoin(update: Update) {
        messageService.editMessagePhoto(
            buildTitlePhotoFile(),
            "Выбор фолопидора для перевода",
            update,
            keyboardService.getFoloCoinKeyboard(update.isUserMessage)
        ).also { logger.debug { "Replied to ${update.chatId.chatIdentity} with folotransfer keyboard" } }
        commandService.foloCoinTransfer(update)
    }

    fun foloIndex(update: Update) {
        LocalDate.now().minusDays(1)
            .let { endDate ->
                foloIndexChartService.buildChart(
                    FoloId.FOLO_CHAT_ID,
                    endDate.minusMonths(1),
                    endDate
                )
            }.let { chart ->
                messageService.editMessagePhoto(
                    chart,
                    "График фолоиндекса за последний месяц",
                    update,
                    keyboardService.getFoloCoinKeyboard(update.isUserMessage)
                )
            }.also { logger.debug { "Replied to ${update.chatId.chatIdentity} with foloindex" } }
    }

    private fun buildTitlePhotoFile() =
        InputFile(
            this::class.java.getResourceAsStream(FOLO_STOCK_IMAGE),
            FOLO_STOCK_IMAGE.substringAfterLast("/")
        )
}