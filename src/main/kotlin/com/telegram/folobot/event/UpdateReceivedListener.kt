package com.telegram.folobot.event

import com.telegram.folobot.service.MessageQueueService
import com.telegram.folobot.service.RegistryService
import com.telegram.folobot.service.handlers.Handler
import mu.KLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update


@Service
class UpdateReceivedListener(
    private val registryService: RegistryService,
    private val messageQueueService: MessageQueueService,
    private val handlers: List<Handler>
) : KLogging() {
    @EventListener
    fun handleUpdateReceived(updateReceivedEvent: UpdateReceivedEvent) {
        val update = updateReceivedEvent.update
        if (update.hasMessage()) {
            //Выполнение независящих от контекста действий
            registryService.register(update)

            //Действие в зависимости от содержимого update
            if (messageQueueService.checkFirstInMediaGroup(update.message?.mediaGroupId))
                onAction(update)
        }
    }

    private fun onAction(update: Update) = handlers.firstOrNull { it.canHandle(update) }?.handle(update)
}