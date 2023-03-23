package com.everbald.folobot.service.folocoin

import com.everbald.folobot.FoloBot
import com.everbald.folobot.config.BotCredentialsConfig
import com.everbald.folobot.extensions.getMsg
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.folocoin.model.InvoicePayload
import com.everbald.folobot.service.folocoin.model.Product
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class InvoiceService(
    private val foloBot: FoloBot,
    private val botCredentials: BotCredentialsConfig,
    private val foloCoinService: FoloCoinService,
    private val messageService: MessageService,
    private val objectMapper: ObjectMapper
) : KLogging() {
    private val issuedInvoices: MutableList<Message> = mutableListOf()
    fun sendInvoice(update: Update): Message? {
        issuedInvoices.forEach { messageService.deleteMessage(it.chatId, it.messageId) }
        issuedInvoices.clear()
        return try {
            foloBot.execute(buildInvoice(update)).also {
                issuedInvoices.add(it)
            }
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending invoice" }
            null
        }
    }

    private fun buildInvoice(update: Update): SendInvoice {
        val price = foloCoinService.getPrice()
        val payload = buildPayload(update, Product.FOLOCOIN, price)
        val labeledPrice = buildLabeledPrice(price)
        return SendInvoice.builder()
            .chatId(update.getMsg().chatId)
            .title(Product.FOLOCOIN.label)
            .description(description)
            .payload(objectMapper.writeValueAsString(payload))
            .providerToken(botCredentials.botProviderToken)
            .currency("RUB")
            .prices(labeledPrice)
            .maxTipAmount(1000 * 100)
            .suggestedTipAmounts(listOf(100 * 100, 200 * 100, 300 * 100, 500 * 100))
            .startParameter("")
            .photoUrl("https://folomkin.ru/images/foloMoney.jpg")
            .photoHeight(750)
            .photoWidth(1000)
            .build()
    }

    private val description =
        "Уважаемый фолопидор, ты можешь круто изменить свою жизнь, став обладателем эксклюзивной мировой валюты - Фолокойна.\n" +
                "После покупки он будет зачислен на баланс твоего фолокошелька"

    private fun buildPayload(update: Update, product: Product, price: Double) =
        InvoicePayload(
            product = product,
            userId = update.getMsg().from.id,
            chatId = update.getMsg().chatId,
            price = price
        )

    private fun buildLabeledPrice(price: Double): List<LabeledPrice> {
        val amount = (price * 100).toInt()
        return listOf(
            LabeledPrice(Product.FOLOCOIN.label, amount),
            LabeledPrice("Скидка фолопидора (-20%)", amount / 100 * -20),
//            LabeledPrice("Распродажа в честь открытия торгов (скидка 10%)", price / 100 * -10)
        )
    }
}