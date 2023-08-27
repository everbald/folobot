package com.everbald.folobot.service.folocoin.sale

import com.everbald.folobot.FoloBot
import com.everbald.folobot.config.BotCredentialsConfig
import com.everbald.folobot.extensions.chatId
import com.everbald.folobot.extensions.isUserMessage
import com.everbald.folobot.extensions.toJson
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.model.InvoicePayload
import com.everbald.folobot.service.folocoin.model.Product
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
    private val messageService: MessageService
) : KLogging() {
    private val issuedInvoices: MutableList<Message> = mutableListOf()

    fun clearInvoices(chatId: Long) {
        issuedInvoices.filter { it.chatId == chatId }.forEach { messageService.deleteMessage(it.chatId, it.messageId) }
        issuedInvoices.removeIf { it.chatId == chatId }
    }

    fun sendInvoice(update: Update): Message? {
        clearInvoices(update.chatId)
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
            .chatId(update.chatId)
            .title(Product.FOLOCOIN.label)
            .description(description)
            .payload(payload.toJson())
            .providerToken(botCredentials.botProviderToken)
            .currency("RUB")
            .prices(labeledPrice)
            .maxTipAmount(1000 * 100)
            .suggestedTipAmounts(listOf(100 * 100, 200 * 100, 300 * 100, 500 * 100))
            .startParameter("folo")
            .protectContent(true)
            .photoUrl("https://folomkin.ru/images/foloMoney.jpg")
            .photoHeight(750)
            .photoWidth(1000)
            .build()
    }

    private val description = "Фолокойн будет зачислен на баланс твоего фолокошелька"

    private fun buildPayload(update: Update, product: Product, price: Double) =
        InvoicePayload(
            product = product,
            price = price,
            chatId = update.chatId,
            isPrivateChat = update.isUserMessage
        )

    private fun buildLabeledPrice(price: Double): List<LabeledPrice> {
        val amount = (price * 100).toInt()
        return listOf(
            LabeledPrice(Product.FOLOCOIN.label, amount),
//            LabeledPrice("Скидка фолопидора (-10%)", amount / 100 * -10)
        )
    }
}