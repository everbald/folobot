package com.everbald.folobot.service.folocoin

import com.everbald.folobot.FoloBot
import com.everbald.folobot.extensions.addOutdatedInvoiceCheckout
import com.everbald.folobot.extensions.getName
import com.everbald.folobot.service.folocoin.model.InvoicePayload
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class PreCheckoutService(
    private val foloBot: FoloBot,
    private val foloCoinService: FoloCoinService,
    private val objectMapper: ObjectMapper
) : KLogging() {
    fun confirmOrder(update: Update) {
        val invoicePrice: InvoicePayload = objectMapper.readValue(update.preCheckoutQuery.invoicePayload)
        val price = foloCoinService.getPrice()
        val isValid = invoicePrice.price == price
        sendConfirmation(update, isValid)
    }

    private fun sendConfirmation(update: Update, isValid: Boolean): Boolean {
        return try {
            foloBot.execute(buildConfirmation(update, isValid))
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
            logger.addOutdatedInvoiceCheckout(update.preCheckoutQuery.from.getName())
        }
        return answer.build()
    }
}