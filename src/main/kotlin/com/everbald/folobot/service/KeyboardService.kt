package com.everbald.folobot.service

import com.everbald.folobot.config.BotCredentialsConfig
import com.everbald.folobot.model.BotCommand
import com.everbald.folobot.model.CallbackCommand
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonRequestUser
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

@Component
class KeyboardService(
    private val botCredentials: BotCredentialsConfig
) : KLogging() {
    fun getFoloCoinKeyboard(isUserMessage: Boolean): InlineKeyboardMarkup = InlineKeyboardMarkup.builder()
        .keyboardRow(listOf(buildFoloMillionaireButton()))
        .keyboardRow(listOf(buildCoinBalanceButton(), buildCoinPriceButton()))
        .keyboardRow(listOf(buildBuyCoinButton(), buildTransferCoinButton(isUserMessage)))
        .build()

    fun buildCoinBalanceButton(): InlineKeyboardButton = InlineKeyboardButton.builder()
        .text(CallbackCommand.COINBALANCE.description)
        .callbackData(CallbackCommand.COINBALANCE.command)
        .build()

    fun buildCoinPriceButton(): InlineKeyboardButton = InlineKeyboardButton.builder()
        .text(CallbackCommand.COINPRICE.description)
        .callbackData(CallbackCommand.COINPRICE.command)
        .build()

    fun buildFoloMillionaireButton(): InlineKeyboardButton = InlineKeyboardButton.builder()
        .text(CallbackCommand.FOLOMILLIONAIRE.description)
        .callbackData(CallbackCommand.FOLOMILLIONAIRE.command)
        .build()

    fun buildBuyCoinButton(): InlineKeyboardButton = InlineKeyboardButton.builder()
        .text(CallbackCommand.BUYCOIN.description)
        .callbackData(CallbackCommand.BUYCOIN.command)
        .pay(true)
        .build()

    fun buildTransferCoinButton(isUserMessage: Boolean): InlineKeyboardButton {
        val button = InlineKeyboardButton.builder()
            .text(CallbackCommand.TRANSFERCOIN.description)
            .callbackData(CallbackCommand.TRANSFERCOIN.command)
        if (!isUserMessage) button.url("https://t.me/${botCredentials.botUsername}?start=${BotCommand.FOLOCOINTRANSFER.command}")
        return button.build()
    }

    fun getFolocoinTransferKeyboard(): ReplyKeyboardMarkup = ReplyKeyboardMarkup.builder()
        .keyboardRow(KeyboardRow(listOf(buildRequestUserButton())))
        .keyboardRow(KeyboardRow(listOf(buildFolocoinTransferButton())))
        .isPersistent(true)
        .resizeKeyboard(true)
        .build()

    fun buildRequestUserButton(): KeyboardButton = KeyboardButton.builder()
        .text("Выбрать фолопидора и перевести")
        .requestUser(
            KeyboardButtonRequestUser.builder()
                .requestId((0..10).random().toString())
                .build()
        )
        .build()

    fun buildFolocoinTransferButton(): KeyboardButton = KeyboardButton.builder()
        .text(BotCommand.FOLOCOINTRANSFERCANCEL.command)
        .build()
}