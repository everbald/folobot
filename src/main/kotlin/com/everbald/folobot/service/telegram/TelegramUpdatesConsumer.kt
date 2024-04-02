package com.everbald.folobot.service.telegram

import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.ReactionService
import com.everbald.folobot.service.RegistryService
import com.everbald.folobot.service.handlers.Handler
import com.everbald.folobot.utils.FoloId
import org.springframework.stereotype.Component
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update

@Component
final class TelegramUpdatesConsumer(
    private val registryService: RegistryService,
    private val reactionService: ReactionService,
    private val messageService: MessageService,
    private val handlers: List<Handler>,
) : LongPollingSingleThreadUpdateConsumer {
    override fun consume(update: Update) {
        try {
            //Выполнение независящих от контекста действий
            registryService.register(update)
            //Реакция на сообщение
            reactionService.react(update)
            //Действие в зависимости от содержимого update
            onAction(update)
        } catch (ex: Exception) {
            messageService.sendMessage(
                "Error \"*${ex.localizedMessage}*\" occurred at ${ex.stackTrace.firstOrNull()}",
                FoloId.FOLO_TEST_CHAT_ID
            )
            throw ex
        }
    }

    private fun onAction(update: Update) = handlers.firstOrNull { it.canHandle(update) }?.handle(update)
}