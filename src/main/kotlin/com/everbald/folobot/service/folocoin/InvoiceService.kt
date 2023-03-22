package com.everbald.folobot.service.folocoin

import com.everbald.folobot.FoloBot
import com.everbald.folobot.config.BotCredentialsConfig
import com.everbald.folobot.extensions.getMsg
import com.everbald.folobot.service.folocoin.model.InvoicePayload
import com.everbald.folobot.service.folocoin.model.Product
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class InvoiceService(
    private val foloBot: FoloBot,
    private val botCredentials: BotCredentialsConfig,
    private val foloCoinService: FoloCoinService,
    private val objectMapper: ObjectMapper
) : KLogging() {
    fun sendInvoice(update: Update): Message? {
        return try {
            foloBot.execute(buildInvoice(update))
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending invoice" }
            null
        }
    }

    private fun buildInvoice(update: Update): SendInvoice {
        val payload = buildPayload(update, Product.FOLOCOIN)
        val prices = buildPrice()
        return SendInvoice.builder()
            .chatId(update.getMsg().chatId)
            .title(Product.FOLOCOIN.label)
            .description(description)
            .payload(objectMapper.writeValueAsString(payload))
            .providerToken(botCredentials.botProviderToken)
            .currency("RUB")
            .prices(prices)

            .maxTipAmount(1000 * 100)
            .suggestedTipAmounts(listOf(100 * 100, 200 * 100, 300 * 100, 500 * 100))
            .startParameter("")
            .photoUrl("https://folomkin.ru/images/foloMoney.jpg")
            .photoHeight(750)
            .photoWidth(1000)
//            .replyMarkup(buildPayButton())
            .build()
    }

    private val description =
        "Уважаемый фолопидор, ты можешь круто изменить свою жизнь, став обладателем эксклюзивной мировой валюты - Фолокойна.\n" +
                "После покупки он будет зачислен на баланс твоего фолокошелька"

    private fun buildPayload(update: Update, product: Product) =
        InvoicePayload(
            product = product,
            userId = update.getMsg().from.id,
            chatId = update.getMsg().chatId
        )

    private fun buildPrice(): List<LabeledPrice> {
        val price = (foloCoinService.getPrice() * 100).toInt()
        return listOf(
            LabeledPrice(Product.FOLOCOIN.label, price),
            LabeledPrice("Скидка фолопидора (-20%)", price / 100 * -20),
//            LabeledPrice("Распродажа в честь открытия торгов (скидка 10%)", price / 100 * -10)
        )
    }

    private fun buildPayButton(): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.builder()
            .keyboardRow(
                listOf(
                    InlineKeyboardButton.builder()
                        .text("Купить ₣")
                        .pay(true)
                        .build()
                )
            )
            .build()
    }
}