package com.everbald.folobot.service.folocoin.sale

import com.everbald.folobot.extensions.addOutdatedInvoiceCheckout
import com.everbald.folobot.extensions.name
import com.everbald.folobot.extensions.toObject
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.model.InvoicePayload
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

@Service
class PreCheckoutService(
    private val telegramClient: TelegramClient,
    private val foloCoinService: FoloCoinService
) : KLogging() {
    fun confirmOrder(update: Update) {
        val invoicePayload: InvoicePayload = update.preCheckoutQuery.invoicePayload.toObject()
        val price = foloCoinService.getPrice()
        val isValid = invoicePayload.price == price
        sendConfirmation(update, isValid)
    }

    private fun sendConfirmation(update: Update, isValid: Boolean): Boolean {
        return try {
            telegramClient.execute(buildConfirmation(update, isValid))
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending pre checkout confirmation" }
            false
        }
    }

    private fun buildConfirmation(update: Update, isValid: Boolean): AnswerPreCheckoutQuery {
        val answer = AnswerPreCheckoutQuery.builder()
            .preCheckoutQueryId(update.preCheckoutQuery.id)
            .ok(isValid)
        if (!isValid) {
            answer.errorMessage("Цена фолокойна изменилась, запросите у бота новый счет")
            logger.addOutdatedInvoiceCheckout(update.preCheckoutQuery.from.name)
        }
        return answer.build()
    }
}