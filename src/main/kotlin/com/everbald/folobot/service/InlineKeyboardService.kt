package com.everbald.folobot.service

import com.everbald.folobot.model.CallbackCommand
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Component
class InlineKeyboardService() : KLogging() {
    fun getfoloCoinKeyboard(): InlineKeyboardMarkup = InlineKeyboardMarkup.builder()
        .keyboardRow(
            listOf(
                InlineKeyboardButton.builder()
                    .text(CallbackCommand.COINBALANCE.description)
                    .callbackData(CallbackCommand.COINBALANCE.command)
                    .build(),
                InlineKeyboardButton.builder()
                    .text(CallbackCommand.COINPRICE.description)
                    .callbackData(CallbackCommand.COINPRICE.command)
                    .build()
            )
        )
        .keyboardRow(
            listOf(
                InlineKeyboardButton.builder()
                    .text(CallbackCommand.FOLOMILLIONAIRE.description)
                    .callbackData(CallbackCommand.FOLOMILLIONAIRE.command)
                    .build(),
                InlineKeyboardButton.builder()
                    .text(CallbackCommand.BUYCOIN.description)
                    .callbackData(CallbackCommand.BUYCOIN.command)
                    .build()
            )
        )
        .build()
}