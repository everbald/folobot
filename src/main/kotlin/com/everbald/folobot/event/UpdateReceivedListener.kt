package com.everbald.folobot.event

import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.RegistryService
import com.everbald.folobot.service.handlers.Handler
import com.everbald.folobot.utils.FoloId
import mu.KLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update


@Service
class UpdateReceivedListener(
    private val registryService: RegistryService,
    private val messageService: MessageService,
    private val handlers: List<Handler>
) : KLogging() {
    @EventListener
    fun handleUpdateReceived(updateReceivedEvent: UpdateReceivedEvent) {
        try {
            updateReceivedEvent.update
                .let {
                    //Выполнение независящих от контекста действий
                    registryService.register(it)
                    //Действие в зависимости от содержимого update
                    onAction(it)
                }
        } catch (ex: Exception) {
            messageService.sendMessage(
                "Error \"*${ex.localizedMessage}*\" occurred at ${ex.stackTrace.firstOrNull()}",
                FoloId.FOLO_TEST_CHAT_ID)
            throw ex
        }
    }

    private fun onAction(update: Update) = handlers.firstOrNull { it.canHandle(update) }?.handle(update)
}