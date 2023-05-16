package com.everbald.folobot.service.handlers

import com.everbald.folobot.extensions.*
import com.everbald.folobot.model.Action
import com.everbald.folobot.model.CallbackCommand
import com.everbald.folobot.service.*
import com.everbald.folobot.service.folocoin.FoloCoinCallbackService
import com.everbald.folobot.service.folopidor.FoloPidorCallbackService
import com.everbald.folobot.utils.FoloId.ANDREW_ID
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

@Component
@Priority(1)
class CallbackHandler(
    private val foloCoinCallbackService: FoloCoinCallbackService,
    private val foloPidorCallbackService: FoloPidorCallbackService,
    private val callbackService: CallbackService,
    private val messageService: MessageService
) : Handler, KLogging() {
    override fun canHandle(update: Update) = CallbackCommand.isMyCommand(update.callbackQuery?.data)
        .also { if (it) logger.addActionReceived(Action.CALLBACKCOMMAND, update.chatId) }

    override fun handle(update: Update) {
        if (update.from.isAndrew()) {
            messageService.sendMessage("Андрей смотрит на фото Фоломкина и мастурбирует", ANDREW_ID)
            callbackService.answerCallbackQuery(update)
            return
        }
        when (
            CallbackCommand.fromCommand(update.callbackQuery.data).also {
                logger.addCallbackCommandReceived(
                    it,
                    getChatIdentity(update.chatId),
                    update.from.getName()
                )
            }
        ) {
            CallbackCommand.COINBALANCE -> foloCoinCallbackService.coinBalance(update)
            CallbackCommand.COINPRICE -> foloCoinCallbackService.coinPrice(update)
            CallbackCommand.FOLOMILLIONAIRE -> foloCoinCallbackService.foloMillionaire(update)
            CallbackCommand.BUYCOIN -> foloCoinCallbackService.buyCoin(update)
            CallbackCommand.TRANSFERCOIN -> foloCoinCallbackService.transferCoin(update)
            CallbackCommand.FOLOINDEX -> foloCoinCallbackService.foloIndex(update)
            CallbackCommand.FOLOPIDOR -> foloPidorCallbackService.foloPidor(update)
            CallbackCommand.FOLOPIDORTOP -> foloPidorCallbackService.foloPidorTop(update)
            CallbackCommand.FOLOSLACKERS -> foloPidorCallbackService.foloSlackers(update)
            CallbackCommand.FOLOUNDERDOGS -> foloPidorCallbackService.foloUnderdogs(update)
            else -> {}
        }
        callbackService.answerCallbackQuery(update)
    }
}