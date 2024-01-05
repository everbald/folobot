package com.everbald.folobot.service.handlers

import com.everbald.folobot.extensions.*
import com.everbald.folobot.domain.type.Action
import com.everbald.folobot.domain.type.CallbackCommand
import com.everbald.folobot.service.*
import com.everbald.folobot.service.folocoin.FoloCoinCallbackService
import com.everbald.folobot.service.folopidor.FoloPidorCallbackService
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

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
        when (
            CallbackCommand.fromCommand(update.callbackQuery.data).also {
                logger.addCallbackCommandReceived(
                    it,
                    update.chatId.chatIdentity,
                    update.from.name
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