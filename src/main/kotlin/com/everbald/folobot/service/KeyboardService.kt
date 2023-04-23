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

    fun buildCallbackButton(callbackCommand: CallbackCommand, pay: Boolean = false): InlineKeyboardButton =
        InlineKeyboardButton.builder()
            .text(callbackCommand.description)
            .callbackData(callbackCommand.command)
            .build()


    fun getFoloCoinKeyboard(isUserMessage: Boolean): InlineKeyboardMarkup = InlineKeyboardMarkup.builder()
        .keyboardRow(listOf(buildCallbackButton(CallbackCommand.FOLOMILLIONAIRE)))
        .keyboardRow(
            listOf(
                buildCallbackButton(CallbackCommand.COINBALANCE),
                buildCallbackButton(CallbackCommand.COINPRICE)
            )
        )
        .keyboardRow(
            listOf(
                buildCallbackButton(CallbackCommand.BUYCOIN, true),
                buildTransferCoinButton(isUserMessage)
            )
        )
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

    fun getFoloPidorKeyboard(): InlineKeyboardMarkup = InlineKeyboardMarkup.builder()
        .keyboardRow(
            listOf(
                buildCallbackButton(CallbackCommand.FOLOPIDOR),
                buildCallbackButton(CallbackCommand.FOLOPIDORTOP)
            )
        )
        .keyboardRow(
            listOf(
                buildCallbackButton(CallbackCommand.FOLOSLACKERS),
                buildCallbackButton(CallbackCommand.FOLOUNDERDOGS)
            )
        )
        .build()
}