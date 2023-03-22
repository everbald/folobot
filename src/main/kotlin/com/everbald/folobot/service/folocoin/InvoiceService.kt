package com.everbald.folobot.service.folocoin

import com.everbald.folobot.FoloBot
import com.everbald.folobot.config.BotCredentialsConfig
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
        val price = (foloCoinService.getPrice() * 100).toInt()
        return SendInvoice.builder()
            .chatId(update.message.chatId)
            .title(Product.FOLOCOIN.label)
            .description("Тут нужен какой то текст про продажу фолокойна, давайте подумаем что будет топово написать тут")
            .payload(objectMapper.writeValueAsString(payload))
            .providerToken(botCredentials.botProviderToken)
            .currency("RUB")
            .price(LabeledPrice(Product.FOLOCOIN.label, price))
            .maxTipAmount(1000 * 100)
            .suggestedTipAmounts(listOf(100 * 100, 200 * 100, 300 * 100, 500 * 100))
            .startParameter("")
            .photoUrl("https://folomkin.ru/images/foloMoney.jpg")
            .needName(true)
            .needPhoneNumber(true)
            .needEmail(true)
            .sendPhoneNumberToProvider(true)
            .sendEmailToProvider(true)
            .replyMarkup(buildPayButton())
            .build()
    }

    private fun buildPayload(update: Update, product: Product) =
        InvoicePayload(
            product = product,
            userId = update.message.from.id,
            chatId = update.message.chatId
        )

    private fun buildPayButton(): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.builder()
            .keyboardRow(
                listOf(
                    InlineKeyboardButton.builder()
                        .text("Записаться на сессию")
                        .pay(true)
                        .build()
                )
            )
            .build()
    }
}